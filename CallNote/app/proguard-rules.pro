# CallNote 混淆规则（第八阶段：R8 + 资源收缩已启用）

# ---- Whisper JNI ----
# native 侧通过 JNI 按类名/方法名查找 Kotlin 对象，禁止混淆或删除
-keep class com.callnote.app.transcription.WhisperNative { *; }

# ---- 崩溃堆栈可读性 ----
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Room / Hilt / WorkManager ----
# 均自带 consumer proguard 规则（AAR 内 proguard.txt），无需手工 keep。
# Room 实体经注解处理生成代码，反射不涉及业务字段名；Hilt 组件类由 kapt 生成并自带规则。
