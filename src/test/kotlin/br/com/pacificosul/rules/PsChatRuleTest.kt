package br.com.pacificosul.rules

import br.com.pacificosul.data.PsChatData
import org.junit.Test
import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import br.com.pacificosul.repository.PsChatRepository
import org.springframework.jdbc.core.JdbcTemplate

class PsChatRuleTest {
    @Test
    fun failAuth() {
        val response = PsChatRule().auth("razor", "123456")
        assert(response.status == "success")
    }

    @Test
    fun integrationUser() {
        val jo = NamedParameterJdbcTemplate(JdbcTemplate(HikariCustomConfig().getOracleTemplate()))
        //  PsChatRepository(jo).cargaInicial()
        val auth = PsChatRule().auth("razor", "123456")
//        PsChatRepository(jo).userIntegration(auth, "41732")
//        PsChatRepository(jo).userIntegration(auth, "41404")
//        PsChatRepository(jo).userIntegration(auth, "40437")
    }

    @Test
    fun getUserInfo() {
        val jo = NamedParameterJdbcTemplate(JdbcTemplate(HikariCustomConfig().getOracleTemplate()))
        //  PsChatRepository(jo).cargaInicial()
        val psChat = PsChatRule()
        val auth = psChat.auth("razor", "123456")
        assert(psChat.getUserId(auth, "41941")?.equals("oN2pY37x8aC2aWSzM")!!)
        assert(psChat.getUserId(auth, "3432413413").isNullOrEmpty())
    }

    @Test
    fun createDarthPaul() {
//        val auth = PsChatRule().auth("razor", "123456")
//        val custom = PsChatData.CustomFieldsData(777, "teste@teste.com", "Sith")
//        val user = PsChatData.UserPayload("Darth Paul", "darth@paul.com", "123456", "darthpaul", custom)
//        PsChatRule().createUser(auth, user)
    }

    @Test
    fun updateUser() {
        val auth = PsChatRule().auth("razor", "123456")
        val custom = PsChatData.CustomFieldsData(null, null, null)
        val data = PsChatData.UpdateUserData(null, null, null, null, custom)
        val id = PsChatRule().getUserId(auth, "darthpaul")
        val user = PsChatData.UpdateUserPayload(id.orEmpty(), data)
        PsChatRule().updateUser(auth, user)
    }
}