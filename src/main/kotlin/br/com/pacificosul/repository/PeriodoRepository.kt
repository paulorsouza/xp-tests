package br.com.pacificosul.repository

import org.springframework.jdbc.core.JdbcTemplate

fun getDescricaoPeriodo(jdbcTemplate: JdbcTemplate, codigoPeriodo: Int): List<String> {
    val sql = "select ifnull(des_periodo,'PRONTA ENTREGA') as descricaoPeriodo from periodo " +
            "where cod_periodo = $codigoPeriodo"
    return jdbcTemplate.query(sql){
        rs,_ -> rs.getString("descricaoPeriodo")
    }.orEmpty()
}