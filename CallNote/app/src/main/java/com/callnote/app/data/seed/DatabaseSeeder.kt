package com.callnote.app.data.seed

import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.dao.TranscriptionTaskDao
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
import com.callnote.app.data.model.TaskStatus
import com.callnote.app.data.model.TranscriptionStatus
import com.callnote.app.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 第一阶段模拟数据：仅在空库时写入一次，用于展示首页统计、录音列表、
 * 分类管理与“我的”页的真实数据形态。第二阶段导入功能上线后仍然保留空库逻辑。
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val categoryDao: CallCategoryDao,
    private val recordDao: CallRecordDao,
    private val taskDao: TranscriptionTaskDao,
    @ApplicationScope private val scope: CoroutineScope
) {

    fun seedIfEmpty() {
        scope.launch {
            if (recordDao.count() > 0) return@launch

            val categoryIds = categoryDao.insertAll(defaultCategories)
            val records = seedRecords(categoryIds)
            val recordIds = recordDao.insertAll(records)

            val tasks = buildList {
                records.forEachIndexed { index, record ->
                    when (record.transcriptionStatus) {
                        TranscriptionStatus.PENDING.name -> add(
                            TranscriptionTaskEntity(
                                recordId = recordIds[index],
                                status = TaskStatus.PENDING.name,
                                createdAt = record.importTime,
                                updatedAt = record.importTime
                            )
                        )

                        TranscriptionStatus.FAILED.name -> add(
                            TranscriptionTaskEntity(
                                recordId = recordIds[index],
                                status = TaskStatus.FAILED.name,
                                retryCount = 1,
                                errorMessage = "音频解码失败：文件可能已损坏或格式不受支持",
                                createdAt = record.importTime,
                                updatedAt = record.updatedAt
                            )
                        )
                    }
                }
            }
            if (tasks.isNotEmpty()) taskDao.insertAll(tasks)
        }
    }

    private val defaultCategories: List<CallCategoryEntity> = listOf(
        CallCategoryEntity(
            categoryName = "工作沟通", categoryCode = "WORK",
            description = "项目、需求、开发、会议、进度", isSystem = true, sort = 1
        ),
        CallCategoryEntity(
            categoryName = "快递外卖", categoryCode = "DELIVERY",
            description = "快递、取件码、外卖、配送、驿站", isSystem = true, sort = 2
        ),
        CallCategoryEntity(
            categoryName = "房产中介", categoryCode = "ESTATE",
            description = "看房、租房、房源、中介费", isSystem = true, sort = 3
        ),
        CallCategoryEntity(
            categoryName = "推销广告", categoryCode = "SPAM",
            description = "贷款、保险、信用卡、投资理财、课程推广", isSystem = true, sort = 4
        ),
        CallCategoryEntity(
            categoryName = "金融业务", categoryCode = "FINANCE",
            description = "银行、账单、还款、额度", isSystem = true, sort = 5
        ),
        CallCategoryEntity(
            categoryName = "亲友通话", categoryCode = "FAMILY",
            description = "家庭日常、生活安排、亲友交流", isSystem = true, sort = 6
        ),
        CallCategoryEntity(
            categoryName = "生活服务", categoryCode = "LIFE",
            description = "物业、维修、水电、家政", isSystem = true, sort = 7
        ),
        CallCategoryEntity(
            categoryName = "其他", categoryCode = "OTHER",
            description = "未能识别类型的通话", isSystem = true, sort = 8
        )
    )

    /** 分类在列表中的位置（1 起），对应插入后的 categoryId */
    private data class SeedRecord(
        val phone: String,
        val daysAgo: Int,
        val hour: Int,
        val minute: Int,
        val durationSeconds: Long,
        val format: String,
        val status: TranscriptionStatus,
        val categoryPos: Int?,
        val text: String?,
        val summary: String?,
        val keywords: String?,
        val score: Float?
    )

    private val seedData: List<SeedRecord> = listOf(
        SeedRecord(
            "13812345678", 3, 14, 25, 62, "MP3", TranscriptionStatus.COMPLETED, 2,
            "您好，您的中通快递已存放在阳光花园小区的兔喜生活驿站，取件码是 6-2-8842，请于今天下午六点前凭取件码取件，超过二十四小时未取将收取保管费。",
            "中通快递已到驿站，取件码 628842，今日 18 点前取件",
            "快递,取件码,驿站,保管费", 0.94f
        ),
        SeedRecord(
            "13987654321", 5, 10, 8, 148, "MP3", TranscriptionStatus.COMPLETED, 4,
            "您好，请问是尾号 6688 的机主吗？我是 XX 财富的理财顾问，现在有一款预期年化收益百分之八的理财产品正在发售，起投金额五万元。另外您符合白金客户条件，办理我们的信用卡可以享受终身免年费。",
            "理财推销：推荐年化 8% 理财产品及信用卡办理",
            "理财,信用卡,年化收益,推销", 0.91f
        ),
        SeedRecord(
            "13666677788", 1, 16, 40, 535, "M4A", TranscriptionStatus.COMPLETED, 1,
            "喂，王工，需求文档我昨天发你了，这个版本要新增离线转写的能力，开发排期大概需要两周。接口文档今晚补上，会议改到明天上午十点在小会议室，你把进度同步一下。测试用例评审别拖，提测定在下周三。",
            "讨论离线转写需求排期、接口文档与提测时间",
            "需求,开发,会议,进度,提测", 0.92f
        ),
        SeedRecord(
            "13511112222", 2, 20, 15, 372, "MP3", TranscriptionStatus.COMPLETED, 6,
            "妈，我这周末可能回不去了，公司项目要上线得加班。你和爸记得按时吃药，降温了多穿点。哦对了，让我给家里买的那台洗脚桶我下单了，过两天到。过年我一定回去，带你们去做个全面体检。",
            "告知周末加班不回家，关心父母身体，过年回家计划",
            "回家,加班,体检,父母", 0.88f
        ),
        SeedRecord(
            "13755556666", 4, 11, 30, 283, "AMR", TranscriptionStatus.COMPLETED, 3,
            "您好，我是链家的小李。您上次看的阳光花园两居室，房东那边松口了，月租可以降两百到三千八。这个房源朝南采光好，周边有地铁口。中介费的话最多收半个月房租。明天下午三点方便再看一次房吗？",
            "中介反馈两居室月租降 200 元，邀约明天看房",
            "看房,租房,房源,中介费", 0.90f
        ),
        SeedRecord(
            "13899990000", 6, 9, 5, 96, "MP3", TranscriptionStatus.COMPLETED, 5,
            "您好，这里是招商银行信用卡中心。您的本期账单已出账，消费金额四千八百五十元，最低还款额四百八十五元，到期还款日为本月二十五日。为避免逾期影响征信，请及时还款。如需账单分期，可以回复短信办理。",
            "招行账单提醒：消费 4850 元，25 日前还款",
            "银行,账单,还款,额度,征信", 0.93f
        ),
        SeedRecord(
            "13644445555", 7, 15, 0, 128, "MP3", TranscriptionStatus.COMPLETED, 7,
            "您好，我是阳光花园物业。您家楼道的水表检测到异常，明天上午九点维修师傅上门检查，麻烦家里留个人。另外看到您家物业费还有两个季度没交，方便的话这周内到物业中心或者线上交一下。",
            "物业通知水表维修及物业费缴纳",
            "物业,维修,水电,物业费", 0.90f
        ),
        SeedRecord(
            "13922223333", 9, 13, 22, 176, "MP3", TranscriptionStatus.COMPLETED, 4,
            "哥，我这边是做贷款的，资金周转需要吗？最高可贷五十万，月息低至三厘，当天申请当天放款，手续简单。您考虑一下，加个微信我把产品详情发给您。",
            "贷款推销：额度 50 万，月息三厘，当天放款",
            "贷款,放款,利息,资金周转", 0.95f
        ),
        SeedRecord(
            "13833334444", 2, 12, 10, 45, "AAC", TranscriptionStatus.COMPLETED, 2,
            "您好，您的外卖到了，我放前台了啊，麻烦尽快取一下，汤别洒了，祝您用餐愉快，麻烦给个五星好评。",
            "外卖送达通知，已放前台",
            "外卖,送达,前台", 0.86f
        ),
        SeedRecord(
            "13577778888", 1, 18, 45, 402, "WAV", TranscriptionStatus.COMPLETED, 1,
            "会议纪要我整理好发你邮箱了。这个迭代的进度整体落后两天，主要是服务端接口延期。你跟一下开发资源，看能不能借调两个人。新需求先别接，优先保上线。下次评审会议定在周五下午。",
            "同步迭代进度落后及开发资源协调安排",
            "会议,进度,开发,需求,上线", 0.93f
        ),
        SeedRecord(
            "13688889999", 8, 19, 30, 264, "MP3", TranscriptionStatus.COMPLETED, 6,
            "爸，体检报告出来了吗？医生怎么说的？血脂还是有点高啊，那个药记得按时吃，别舍不得钱。下周三我请了半天假，陪你去医院复查，我直接挂好号，你别管了。",
            "父亲体检血脂偏高，下周三陪诊复查",
            "体检,复查,医院,父亲", 0.89f
        ),
        SeedRecord(
            "13711112222", 10, 14, 5, 198, "OGG", TranscriptionStatus.COMPLETED, 3,
            "这套房源说实话采光一般，租金三千五有点虚高，同小区还有两套类似的。中介费最多接受半个月房租，高了就找别家。回头你把那两套房源的照片发我对比一下。",
            "对比房源租金与中介费，要求发照片对比",
            "房源,租金,中介费,看房", 0.87f
        ),
        SeedRecord(
            "13866667777", 12, 10, 0, 611, "M4A", TranscriptionStatus.COMPLETED, 1,
            "面试官您好，我做 Android 开发五年了，主要技术栈是 Kotlin，近两年在用 Jetpack Compose 做声明式界面。性能优化方面做过启动速度和内存治理，也带队做过组件化改造。期望薪资的话，可以按贵公司的标准来谈。",
            "Android 开发岗位技术面试沟通",
            "开发,面试,Kotlin,项目", 0.85f
        ),
        SeedRecord(
            "13955551111", 4, 8, 50, 108, "MP3", TranscriptionStatus.COMPLETED, 7,
            "阿姨，明天还是老时间九点过来打扫哈。这次重点擦一下厨房的窗户和油烟机，客厅沙发底下也扫一下。对了，入户门密码没变，直接进来就行。",
            "预约家政保洁，重点清洁厨房窗户与油烟机",
            "家政,打扫,保洁", 0.90f
        ),
        SeedRecord(
            "13900001111", 6, 21, 12, 75, "MP3", TranscriptionStatus.COMPLETED, 8,
            "喂？喂？能听到吗？这边信号不太好……你说什么？我听不清……算了算了，回头再打，就这样。",
            "通话信号不佳，内容无法识别",
            "信号,听不清", 0.31f
        ),
        SeedRecord(
            "13622224444", 0, 9, 40, 88, "MP3", TranscriptionStatus.PENDING, null,
            null, null, null, null
        ),
        SeedRecord(
            "13877772222", 0, 11, 15, 203, "M4A", TranscriptionStatus.PENDING, null,
            null, null, null, null
        ),
        SeedRecord(
            "13988883333", 1, 17, 20, 156, "AMR", TranscriptionStatus.PENDING, null,
            null, null, null, null
        ),
        SeedRecord(
            "13533335555", 3, 10, 30, 395, "AMR", TranscriptionStatus.FAILED, null,
            null, null, null, null
        )
    )

    private fun seedRecords(categoryIds: List<Long>): List<CallRecordEntity> {
        val nameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        return seedData.map { seed ->
            val callTime = ZonedDateTime.now()
                .minusDays(seed.daysAgo.toLong())
                .withHour(seed.hour)
                .withMinute(seed.minute)
                .withSecond(0)
                .toInstant()
                .toEpochMilli()
            val importTime = callTime + 90_000L
            val finishTime = importTime + seed.durationSeconds * 4_000L
            CallRecordEntity(
                fileName = "通话录音_${seed.phone}_${nameFormat.format(Date(callTime))}.${seed.format.lowercase(Locale.CHINA)}",
                fileUri = "content://media/external/audio/media/${1000 + seed.phone.takeLast(4).toInt()}",
                fileSize = estimateSize(seed.durationSeconds, seed.format),
                duration = seed.durationSeconds,
                format = seed.format,
                callTime = callTime,
                importTime = importTime,
                transcriptionStatus = seed.status.name,
                transcriptionText = seed.text,
                categoryId = seed.categoryPos?.let { categoryIds[it - 1] },
                summary = seed.summary,
                keywords = seed.keywords,
                classificationScore = seed.score,
                createdAt = importTime,
                updatedAt = if (seed.status == TranscriptionStatus.COMPLETED) finishTime else importTime
            )
        }
    }

    private fun estimateSize(durationSeconds: Long, format: String): Long = when (format) {
        "AMR" -> durationSeconds * 1_600L
        "WAV" -> durationSeconds * 32_000L
        else -> durationSeconds * 16_000L
    }
}
