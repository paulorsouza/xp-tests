package br.com.pacificosul.rules

import org.junit.Test
import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import br.com.pacificosul.repository.PsChatRepository
import org.springframework.jdbc.core.JdbcTemplate

class PsChatRuleTest {
    @Test
    fun failAuth() {
        val response = PsChatRule().auth("teste", "teste")
        assert(response.status != "success")
    }

    @Test
    fun integrationUser() {
        val jo = NamedParameterJdbcTemplate(JdbcTemplate(HikariCustomConfig().getOracleTemplate()))
        //  PsChatRepository(jo).cargaInicial()
        val auth = PsChatRule().auth("razor", "teste")
//        PsChatRepository(jo).userIntegration(auth, "41455")
//        PsChatRepository(jo).userIntegration(auth, "40432")
//        PsChatRepository(jo).userIntegration(auth, "40437")
    }

    @Test
    fun getUserInfo() {
        val jo = NamedParameterJdbcTemplate(JdbcTemplate(HikariCustomConfig().getOracleTemplate()))
        //  PsChatRepository(jo).cargaInicial()
        val psChat = PsChatRule()
        val auth = psChat.auth("razor", "123456")
        assert(psChat.userExists(auth, "41941"))
        assert(!psChat.userExists(auth, "3432413413"))
    }

}