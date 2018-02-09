package br.com.pacificosul.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PrioridadeOpRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun hasPrioridade(op: Int): Boolean {
        val sql = "select 1 from pacificosul.ps_tb_op_uti a " +
                "where a.ordem_producao = :ordem_producao "
        val mapa = HashMap<String, Any>()
        mapa["ordem_producao"] = op
        val result = jdbcTemplate.query(sql, mapa) { rs, _ ->
            rs.getString(1).isNotEmpty()
        }.firstOrNull()
        return result != null && result
    }

    fun allowUnmarkPrioridade(op: Int): Boolean {
        val sql = "select 1 from pacificosul.ps_tb_op_uti a " +
                "where a.ordem_producao = :ordem_producao " +
                "and dat_saida is null "
        val mapa = HashMap<String, Any>()
        mapa["ordem_producao"] = op
        val result = jdbcTemplate.query(sql, mapa) { rs, _ ->
            rs.getString(1).isNotEmpty()
        }.firstOrNull()
        return result != null && result
    }

    fun markPrioridade(op: Int, grupo: String, codUsuario: Int): Int {
        val sql = "insert into pacificosul.ps_tb_op_uti " +
                "(ordem_producao, sit_ativo, nivel, grupo, dat_entrada, cod_usuario_entrada) " +
                "values " +
                "(:op, 1 , '1', :grupo, sysdate, :cod_usuario) "

        val mapa = HashMap<String, Any>()
        mapa["op"] = op
        mapa["cod_usuario"] = codUsuario
        mapa["grupo"] = grupo
        return jdbcTemplate.update(sql, mapa)
    }

    fun unmarkPrioridade(op: Int, codUsuario: Int): Int {
        val sql = "update pacificosul.ps_tb_op_uti " +
                "set sit_ativo = 0, " +
                "    data_saida = sysdate, " +
                "    cod_usuario_saida = :cod_usuario " +
                "where a.ordem_producao = :ordem_producao " +
                "  and dat_saida is null "
        val mapa = HashMap<String, Any>()
        mapa["ordem_producao"] = op
        mapa["cod_usuario"] = codUsuario
        return jdbcTemplate.update(sql, mapa)
    }

}