package br.com.pacificosul.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class GridProfileRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun getCurrentProfile(gridName: String, codUser: Int) {
        val sql = "select "
    }
}

//fun listAll(ordemProducao: Int): List<ObservacaoData> {
//    val sql = "select * from pacificosul.ps_tb_obs_200 " +
//            "where ordem_producao = :ordemProducao " +
//            "order by data_observacao desc"
//
//    val mapa = HashMap<String, Any>()
//    mapa["ordemProducao"] = ordemProducao
//
//    return jdbcTemplate.query(sql, mapa) {
//        rs, _ -> ObservacaoData(
//            ordemProducao, rs.getString("usuario"), rs.getString("observacao"),
//            rs.getInt("sequencia"), rs.getString("estagio"), rs.getDate("data_observacao"))
//    }.orEmpty()
//}