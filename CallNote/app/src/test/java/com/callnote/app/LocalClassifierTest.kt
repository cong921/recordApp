package com.callnote.app

import com.callnote.app.data.classification.LocalClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 本地规则分类器测试：词典命中、置信度、摘要与关键词提取 */
class LocalClassifierTest {

    private val classifier = LocalClassifier()

    @Test
    fun `快递文本命中 DELIVERY 分类`() {
        val text = "您好，您的快递已到达菜鸟驿站，请凭取货码及时取件。包裹今天送达，请保持电话畅通。"
        val result = classifier.classify(text)
        assertEquals("DELIVERY", result.categoryCode)
        assertTrue(result.score > 0)
        assertTrue(result.keywords.isNotEmpty())
    }

    @Test
    fun `推销文本命中 SPAM 分类`() {
        val text = "先生您好，我们这边有一个专属优惠活动，限时折扣，免费领取名额有限，点击链接即可办理。"
        val result = classifier.classify(text)
        assertEquals("SPAM", result.categoryCode)
    }

    @Test
    fun `工作文本命中 WORK 分类`() {
        val text = "下午三点有个会议，主要对一下项目进度和需求排期，客户那边合同和报价也要评审。"
        val result = classifier.classify(text)
        assertEquals("WORK", result.categoryCode)
    }

    @Test
    fun `无命中文本返回未分类`() {
        val text = "今天天气不错，我们聊聊哲学和宇宙的起源吧。"
        val result = classifier.classify(text)
        assertNull(result.categoryCode)
        assertEquals(0.0, result.score, 0.001)
    }

    @Test
    fun `空文本返回未分类且摘要为空`() {
        val result = classifier.classify("   ")
        assertNull(result.categoryCode)
        assertEquals("", result.summary)
        assertTrue(result.keywords.isEmpty())
    }

    @Test
    fun `摘要取首句并在短句时补第二句`() {
        val text = "喂，你好。我想问一下快递什么时候到。麻烦放到驿站谢谢。"
        val summary = classifier.summarize(text)
        // 首句"喂，你好"太短，应补第二句
        assertTrue(summary.startsWith("喂，你好"))
        assertTrue(summary.contains("快递"))
    }

    @Test
    fun `单句无标点文本摘要为原文截断`() {
        val text = "明天记得回家吃饭"
        assertEquals(text, classifier.summarize(text))
    }

    @Test
    fun `命中次数影响置信度`() {
        val weak = classifier.classify("您的快递到了。")
        val strong = classifier.classify(
            "您的快递到了，包裹在驿站。取件码稍后发送，快递今天送达，请及时取件签收。"
        )
        assertTrue(strong.score > weak.score)
        assertTrue(strong.score <= 1.0)
    }
}
