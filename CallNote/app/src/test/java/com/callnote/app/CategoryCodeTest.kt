package com.callnote.app

import com.callnote.app.data.repository.CallCategoryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 自定义分类编码生成测试（第六阶段分类管理） */
class CategoryCodeTest {

    @Test
    fun `编码以 CUSTOM 前缀开头`() {
        val code = CallCategoryRepository.generateCategoryCode("老同学")
        assertTrue(code.startsWith("CUSTOM_"))
    }

    @Test
    fun `同名多次生成编码互不相同`() {
        val first = CallCategoryRepository.generateCategoryCode("客户回访")
        val second = CallCategoryRepository.generateCategoryCode("客户回访")
        assertNotEquals(first, second)
    }

    @Test
    fun `编码仅含字母数字与下划线`() {
        val code = CallCategoryRepository.generateCategoryCode("测试-分类/01")
        assertTrue(code.matches(Regex("CUSTOM_[0-9a-f]+_\\d+")))
    }

    @Test
    fun `空名也能生成合法编码`() {
        val code = CallCategoryRepository.generateCategoryCode("")
        assertTrue(code.startsWith("CUSTOM_"))
        assertEquals("CUSTOM_0_", code.substring(0, 9))
    }
}
