package br.com.pacificosul.rules

import org.junit.Test
import kotlin.test.assertEquals

class PsChatRuleTest {
    @Test
    fun doAuth() {
        val response = PsChatRule().auth()
        assert(response.status == "success")
    }
}