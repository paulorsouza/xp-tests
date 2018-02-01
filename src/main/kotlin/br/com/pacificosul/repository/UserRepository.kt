package br.com.pacificosul.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun sameIp(codUser: Int, ip: String): Boolean {
        val sql = "select 1 from pacificosul.ps_tb_usuario " +
                "where ip_web = :ip and cod_usuario = :codigoUsuario "

        val mapa = HashMap<String, Any>()
        mapa["ip"] = ip
        mapa["codigoUsuario"] = codUser

        val result = jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString(1)
        }.orEmpty()

        if(result.isNotEmpty()) return true
        return false
    }

    fun getApelido(cracha: Int): String? {
        val sql = "select des_apelido from pacificosul.ps_tb_usuario " +
                "where cod_usuario_vetorh = :codigoUsuarioVetorH "
        val mapa = HashMap<String, Any>()
        mapa["codigoUsuarioVetorH"] = cracha
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString(1)
        }.firstOrNull()
    }

    fun getNomeUsuario(codUser: Int): String? {
        val sql = "select des_usuario from pacificosul.ps_tb_usuario " +
                "where cod_usuario = :codigoUsuario "
        val mapa = HashMap<String, Any>()
        mapa["codigoUsuario"] = codUser
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString(1)
        }.firstOrNull()
    }
}