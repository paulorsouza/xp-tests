package br.com.pacificosul.repository

import br.com.pacificosul.data.PsChatData.UserAuth
import br.com.pacificosul.data.PsChatData.CustomFieldsData
import br.com.pacificosul.data.PsChatData.UserPayload
import br.com.pacificosul.rules.PsChatRule
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class PsChatRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun cargaInicial() {
        val auth = PsChatRule().auth("senha", "semsenha")
        val sql = "select a.cod_usuario_vetorh, des_usuario, a.des_email, " +
                "a.des_senha, descricao_setor, a.num_ramal, a.sit_usuario, a.tem_rocketchat " +
                "from pacificosul.ps_tb_usuario a " +
                "left join vetorh.r034fun b on (a.cod_usuario_vetorh = b.numcad and a.num_empresa = b.numemp) " +
                "left join systextil.basi_006 c on (b.numloc = c.setor_responsavel) " +
                "left join vetorh.r024car d on (b.codcar = d.codcar) " +
                "left join systextil.efic_050 f on (b.numcad = f.cracha_funcionario) " +
                "where b.numemp = 17 " +
                "  and a.sit_usuario = 'A' and a.tem_rocketchat = 1 "
        jdbcTemplate.query(sql) { rs, _ ->
            val userData = mountUserPayload(rs)
            PsChatRule().createUser(auth, userData)
        }
    }

    fun userIntegration(auth: UserAuth, cracha: String) {
        val sql = "select a.cod_usuario_vetorh, des_usuario, a.des_email, " +
                  "a.des_senha, descricao_setor, a.num_ramal " +
                  "from pacificosul.ps_tb_usuario a " +
                  "left join vetorh.r034fun b on (a.cod_usuario_vetorh = b.numcad and a.num_empresa = b.numemp) " +
                  "left join systextil.basi_006 c on (b.numloc = c.setor_responsavel) " +
                  "left join vetorh.r024car d on (b.codcar = d.codcar) " +
                  "left join systextil.efic_050 f on (b.numcad = f.cracha_funcionario) " +
                  "where b.numemp = 17 " +
                  "  and a.sit_usuario = 'A' and a.tem_rocketchat = 1 " +
                  "  and a.cod_usuario_vetorh = :cracha "
        val mapa = HashMap<String, Any>()
        mapa["cracha"] = cracha

        val userData = jdbcTemplate.query(sql, mapa) {
            rs, _ -> mountUserPayload(rs)
        }.firstOrNull()

        PsChatRule().createUser(auth, userData!!)
    }

    fun mountUserPayload(rs: ResultSet): UserPayload {
        val cod = rs.getString("COD_USUARIO_VETORH")
        val des = transformName(rs.getString("DES_USUARIO"))
        val senha = rs.getString("DES_SENHA")
        val local = transformLocal(rs.getString("descricao_setor"))
        val numRamal = rs.getString("num_ramal")
        val email = rs.getString("des_email").orEmpty()

        val customFields = CustomFieldsData(numRamal, email, local)
        return UserPayload(des, "${cod}@${cod}.com", senha, cod, customFields)
    }

    fun transformName(name: String): String {
        return name.toLowerCase().split(" ").joinToString(separator = " ", transform = { s -> s.capitalize() })
    }

    fun transformLocal(local: String): String {
        return if(local == "TI") "Tecnologia" else local.toLowerCase().capitalize()
    }
}