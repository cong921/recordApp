package com.callnote.app.data.classification

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 智能分类结果。
 * @param categoryCode 命中分类编码；null 表示无足够证据，保持未分类
 * @param score 置信度 0..1
 * @param keywords 命中的关键词（按出现次数降序）
 * @param summary 提取式摘要
 */
data class ClassificationResult(
    val categoryCode: String?,
    val score: Double,
    val keywords: List<String>,
    val summary: String
)

/**
 * 本地规则分类器：词典命中计数 + 提取式摘要，纯 CPU 毫秒级完成。
 * 不依赖任何网络与大模型，符合产品离线原则。
 */
@Singleton
class LocalClassifier @Inject constructor() {

    /** 对转写文本执行分类与摘要 */
    fun classify(text: String): ClassificationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ClassificationResult(null, 0.0, emptyList(), "")
        }

        // 1. 各分类词典命中统计
        val hitCounts = mutableMapOf<String, MutableMap<String, Int>>()
        for ((code, dictionary) in ClassificationRules.dictionaries) {
            val hits = mutableMapOf<String, Int>()
            for (keyword in dictionary) {
                val count = countOccurrences(trimmed, keyword)
                if (count > 0) hits[keyword] = count
            }
            if (hits.isNotEmpty()) hitCounts[code] = hits
        }

        // 2. 取命中次数最多的分类
        val best = hitCounts.maxByOrNull { it.value.values.sum() }
        val categoryCode: String?
        val score: Double
        val keywords: List<String>
        if (best == null || best.value.values.sum() == 0) {
            categoryCode = null
            score = 0.0
            keywords = emptyList()
        } else {
            categoryCode = best.key
            val totalHits = best.value.values.sum()
            score = (totalHits.toDouble() / ClassificationRules.FULL_SCORE_HITS)
                .coerceAtMost(1.0)
            keywords = best.value.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                .take(ClassificationRules.KEYWORD_LIMIT)
                .map { it.key }
        }

        return ClassificationResult(
            categoryCode = categoryCode,
            score = score,
            keywords = keywords,
            summary = summarize(trimmed)
        )
    }

    /** 统计子串出现次数（简单遍历，文本量在通话级别，性能足够） */
    private fun countOccurrences(text: String, keyword: String): Int {
        if (keyword.isEmpty()) return 0
        var count = 0
        var index = text.indexOf(keyword)
        while (index >= 0) {
            count++
            index = text.indexOf(keyword, index + keyword.length)
        }
        return count
    }

    /**
     * 提取式摘要：中文按句号/问号/感叹号分句，
     * 优先取第一句，太短则补第二句，截断到上限。
     */
    fun summarize(text: String): String {
        val sentences = text.split('。', '！', '？', '!', '?', '；', ';', '\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (sentences.isEmpty()) {
            return text.take(ClassificationRules.SUMMARY_MAX_LENGTH)
        }
        val builder = StringBuilder()
        for (sentence in sentences) {
            if (builder.isNotEmpty()) builder.append("。")
            builder.append(sentence)
            if (builder.length >= 20) break // 首句够长即可
        }
        if (builder.isNotEmpty() && !builder.endsWith("。") &&
            builder.length < text.length
        ) {
            builder.append("……")
        }
        return builder.toString().take(ClassificationRules.SUMMARY_MAX_LENGTH)
    }
}
