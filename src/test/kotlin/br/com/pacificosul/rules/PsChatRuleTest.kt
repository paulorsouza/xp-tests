package br.com.pacificosul.rules

import org.junit.Test
import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import br.com.pacificosul.repository.PsChatRepository
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals

class PsChatRuleTest {
    @Test
    fun doAuth() {
        val response = PsChatRule().auth()
        assert(response.status == "success")
    }

    @Test
    fun createUser() {
        val jo = JdbcTemplate(HikariCustomConfig().getOracleTemplate())
        PsChatRepository(jo).insert()
    }
}