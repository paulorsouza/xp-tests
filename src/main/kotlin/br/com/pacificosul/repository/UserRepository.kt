package br.com.pacificosul.repository

import org.springframework.jdbc.core.JdbcTemplate

class UserRepository(private val jdbcTemplate: JdbcTemplate) {

    fun sameIp(codUser: Int, ip: String): Boolean {
        val sql = "select 1 from pacificosul.ps_tb_usuario " +
                "where ip_web = ? and cod_usuario = ? "
        val result = jdbcTemplate.query(sql, arrayOf(ip, codUser)) {
            rs, _ -> rs.getString(1)
        }.orEmpty()

        if(result.isNotEmpty()) return true
        return false
    }

    fun getApelido(codUser: Int): String? {
        val sql = "select des_apelido from pacificosul.ps_tb_usuario " +
                "where cod_usuario_vetorh = ? "
        return jdbcTemplate.query(sql, arrayOf(codUser)) {
            rs, _ -> rs.getString(1)
        }.firstOrNull()
    }
}