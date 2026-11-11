#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
CallNote（听记）真机自动化测试脚本
================================
在你自己的电脑上运行（手机 USB 连接 + 已开启 USB 调试）。

用法：
    python3 callnote_device_test.py
    python3 callnote_device_test.py --apk app-debug.apk --adb /path/to/adb

脚本自动完成：安装 APK → 冷启动 → 崩溃检查 → 四页面导航与文案校验 →
无模型转写提示 → 冷重启稳定性 → 输出测试报告 + 截图。

测完把同目录生成的 callnote_test_report.txt 内容发回去即可。
"""

import argparse
import os
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PKG = "com.callnote.app"
ACTIVITY = "com.callnote.app/.MainActivity"
REPORT_LINES = []


def log(msg):
    print(msg, flush=True)
    REPORT_LINES.append(msg)


def result(name, ok, detail=""):
    mark = "[PASS]" if ok else "[FAIL]"
    log(f"{mark} {name}" + (f" —— {detail}" if detail else ""))
    return ok


# ---------------------------------------------------------------- adb 封装
class Adb:
    def __init__(self, path):
        self.path = path

    def run(self, *args, timeout=90, binary=False):
        p = subprocess.run([self.path, *args], capture_output=True, timeout=timeout)
        if binary:
            return p.stdout
        return p.stdout.decode("utf-8", "replace").strip()

    def shell(self, cmd, **kw):
        return self.run("shell", cmd, **kw)


def find_adb(explicit):
    candidates = []
    if explicit:
        candidates.append(explicit)
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if sdk:
        candidates += [os.path.join(sdk, "platform-tools", n) for n in ("adb.exe", "adb")]
    candidates += ["adb.exe", "adb"]
    for c in candidates:
        try:
            subprocess.run([c, "version"], capture_output=True, timeout=15)
            return c
        except Exception:
            continue
    sys.exit(
        "找不到 adb。请安装 Android platform-tools：\n"
        "  https://developer.android.com/tools/releases/platform-tools\n"
        "或用 --adb 指定路径（Android Studio 用户通常在\n"
        "  <SDK目录>/platform-tools/adb 下）。"
    )


# ---------------------------------------------------------------- UI dump
def uidump(adb):
    """抓取当前屏幕控件树，返回 ElementTree root；失败重试 3 次。"""
    for _ in range(3):
        adb.shell("uiautomator dump /sdcard/callnote_ui.xml")
        raw = adb.run("exec-out", "cat", "/sdcard/callnote_ui.xml", binary=True, timeout=30)
        if raw and b"<hierarchy" in raw:
            try:
                return ET.fromstring(raw.decode("utf-8", "replace"))
            except ET.ParseError:
                pass
        time.sleep(2)
    return None


def parse_bounds(b):
    m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", b or "")
    if not m:
        return None
    x1, y1, x2, y2 = map(int, m.groups())
    return (x1 + x2) // 2, (y1 + y2) // 2


def find_text_nodes(root, exact=None, contains=None):
    """返回 [(text, (cx, cy))]；多个命中时取屏幕最下方（底部导航优先）。"""
    hits = []
    if root is None:
        return hits
    for node in root.iter("node"):
        t = node.get("text") or node.get("content-desc") or ""
        if (exact is not None and t == exact) or (contains is not None and contains in t):
            c = parse_bounds(node.get("bounds"))
            if c:
                hits.append((t, c))
    return hits


def all_texts(root):
    if root is None:
        return ""
    return "\n".join(
        t for t in (n.get("text") or n.get("content-desc") or "" for n in root.iter("node")) if t
    )


def tap_text(adb, root, exact=None, contains=None):
    """按文案点击（默认取最下方的命中项，兼容底部导航）。"""
    hits = find_text_nodes(root, exact=exact, contains=contains)
    if not hits:
        return False
    _, (cx, cy) = max(hits, key=lambda h: h[1][1])
    adb.shell(f"input tap {cx} {cy}")
    return True


def screenshot(adb, path):
    png = adb.run("exec-out", "screencap", "-p", binary=True, timeout=30)
    if png and png[:4] == b"\x89PNG":
        with open(path, "wb") as f:
            f.write(png)
        return True
    return False


def crash_scan(adb):
    """读取 crash 专用缓冲区（完整保留 FATAL EXCEPTION 堆栈）。"""
    out = adb.run("logcat", "-d", "-b", "crash", timeout=30)
    return "FATAL EXCEPTION" in out, out


def save_crash_log(adb, here):
    crashed, out = crash_scan(adb)
    if crashed:
        path = os.path.join(here, "callnote_crash_log.txt")
        with open(path, "w", encoding="utf-8") as f:
            f.write(out)
        log(f"完整崩溃日志已保存：{path}（请一并发回）")
    return crashed, out


# ---------------------------------------------------------------- 主流程
def main():
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass

    here = os.path.dirname(os.path.abspath(__file__))
    parser = argparse.ArgumentParser(description="CallNote 真机自动化测试")
    parser.add_argument("--apk", default=os.path.join(here, "app-debug.apk"))
    parser.add_argument("--adb", default=None)
    args = parser.parse_args()

    adb = Adb(find_adb(args.adb))

    log("=" * 56)
    log("CallNote（听记）真机自动化测试  " + time.strftime("%Y-%m-%d %H:%M:%S"))
    log("=" * 56)

    # 1. 设备
    online = [d for d in adb.run("devices").splitlines()
              if "\tdevice" in d and not d.startswith("*")]
    if not online:
        result("设备连接", False,
               "未检测到设备。检查：USB 调试已开启、手机上已信任此电脑。\n"
               + adb.run("devices"))
        return finish(here, False)
    serial = online[0].split("\t")[0]
    model = adb.shell("getprop ro.product.model")
    android_ver = adb.shell("getprop ro.build.version.release")
    result("设备连接", True, f"{model}（Android {android_ver}, {serial}）")

    # 2. 安装
    if not os.path.isfile(args.apk):
        result("APK 文件", False, f"找不到 {args.apk}")
        return finish(here, False)
    install_out = adb.run("install", "-r", args.apk, timeout=180)
    ok = "Success" in install_out
    result("安装 APK", ok, install_out.splitlines()[-1] if install_out else "")
    if not ok:
        return finish(here, False)

    # 3. 版本确认 + 通知权限预授权（Android 13+ 弹窗会挡住界面）
    ver = adb.shell(f"dumpsys package {PKG} | grep versionName")
    result("版本确认", "versionName=1.0.0" in ver, ver.splitlines()[0] if ver else "")
    adb.shell(f"pm grant {PKG} android.permission.POST_NOTIFICATIONS")

    # 4. 冷启动
    adb.run("logcat", "-c")
    adb.run("logcat", "-b", "crash", "-c")
    adb.shell(f"am start -n {ACTIVITY}")
    time.sleep(6)
    crashed, _ = save_crash_log(adb, here)
    result("冷启动无崩溃", not crashed, "见 callnote_crash_log.txt" if crashed else "")

    # 5. 首页
    root = uidump(adb)
    texts = all_texts(root)
    home_ok = ("听记" in texts and "快捷操作" in texts and "批量转写" in texts)
    result("首页渲染（标题/统计/快捷操作）", home_ok)
    screenshot(adb, os.path.join(here, "shot_1_home.png"))

    # 6. 点批量转写 → 应出现三种提示之一（未装模型/无待转写/已开始）
    if not tap_text(adb, root, exact="批量转写"):
        adb.shell("input swipe 540 1600 540 600")  # 兜底下滑
        time.sleep(1)
        root = uidump(adb)
        tap_text(adb, root, exact="批量转写")
    root2 = None
    snackbar_hit = None
    for _ in range(2):
        time.sleep(1)
        root2 = uidump(adb)
        texts = all_texts(root2)
        snackbar_hit = next(
            (m for m in ("请先在", "没有待转写的录音", "已开始本地转写") if m in texts), None
        )
        if snackbar_hit:
            break
    detail = {
        "请先在": "未下载模型 → 拦截提示",
        "没有待转写的录音": "模型已就绪、无待转写记录",
        "已开始本地转写": "转写任务已调度",
    }.get(snackbar_hit, "未捕获到 Snackbar（可能为抓取时序问题）")
    result("批量转写状态提示", snackbar_hit is not None, detail)

    # 7. 录音页
    tap_text(adb, root2, exact="录音")
    time.sleep(2)
    root = uidump(adb)
    texts = all_texts(root)
    rec_ok = "搜索文件名或转写内容" in texts and ("共" in texts or "暂无录音" in texts)
    result("录音页渲染（列表/搜索框）", rec_ok)
    screenshot(adb, os.path.join(here, "shot_2_records.png"))

    # 8. 分类页（预置 8 个系统分类）
    tap_text(adb, root, exact="分类")
    time.sleep(2)
    root = uidump(adb)
    texts = all_texts(root)
    # 只断言首屏可见内容（“其他”等排在列表下方，折叠区外抓不到）
    cat_ok = all(k in texts for k in ("工作沟通", "快递外卖"))
    result("分类页渲染（系统分类种子数据）", cat_ok)
    screenshot(adb, os.path.join(here, "shot_3_categories.png"))

    # 9. 我的页
    tap_text(adb, root, exact="我的")
    time.sleep(2)
    root = uidump(adb)
    texts = all_texts(root)
    # 只断言首屏可见内容（“功能规划”在引擎卡下方，可能超出屏幕）
    me_ok = ("语音转写引擎" in texts and "Whisper.cpp" in texts)
    result("我的页渲染（引擎卡/功能规划）", me_ok)
    screenshot(adb, os.path.join(here, "shot_4_profile.png"))

    # 10. 数据库落盘（debug 包可用 run-as 验证 Room 建库）
    db_out = adb.shell(f"run-as {PKG} ls databases")
    result("Room 数据库落盘", "callnote" in db_out, db_out.replace("\n", "  ")[:120])

    # 11. 冷重启稳定性（先清 crash 缓冲区，只统计重启后新产生的崩溃）
    adb.run("logcat", "-b", "crash", "-c")
    adb.shell(f"am force-stop {PKG}")
    time.sleep(2)
    adb.shell(f"am start -n {ACTIVITY}")
    time.sleep(6)
    crashed, _ = save_crash_log(adb, here)
    restart_ok = (not crashed) and "听记" in all_texts(uidump(adb))
    result("冷重启稳定性", restart_ok, "见 callnote_crash_log.txt" if crashed else "")

    screenshot(adb, os.path.join(here, "shot_5_restart.png"))
    finish(here, True)


def finish(here, started):
    log("-" * 56)
    fails = [l for l in REPORT_LINES if l.startswith("[FAIL]")]
    log(f"结论：{'全部通过' if not fails and started else str(len(fails)) + ' 项失败'}")
    report = os.path.join(here, "callnote_test_report.txt")
    with open(report, "w", encoding="utf-8") as f:
        f.write("\n".join(REPORT_LINES) + "\n")
    print(f"\n报告已生成：{report}")
    print("请把该文件内容发回给沙箱里的助手；截图（shot_*.png）可一并上传。")


if __name__ == "__main__":
    main()
