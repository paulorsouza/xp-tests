package br.com.pacificosul.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PrioridadeOpRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun temPrioridade(op: Int): Boolean {
        val sql = "select 1 from pacificosul.ps_tb_op_uti a " +
                "where a.ordem_producao = :ordem_producao "
        val mapa = HashMap<String, Any>()
        mapa["ordem_producao"] = op
        val result = jdbcTemplate.query(sql, mapa) { rs, _ ->
            rs.getString(1).isNotEmpty()
        }.firstOrNull()
        return result != null && result
    }

    fun permiteDesmarcarPrioridade(op: Int): Boolean {
        println(op)
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

    fun marcarPrioridade(op: Int, grupo: String, codUsuario: Int): Int {
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

    fun desmarcarPrioridade(op: Int, codUsuario: Int): Int {
        println(op)
        val sql = "update pacificosul.ps_tb_op_uti " +
                "set sit_ativo = 0, " +
                "    dat_saida = sysdate, " +
                "    cod_usuario_saida = :cod_usuario " +
                "where ordem_producao = :ordem_producao " +
                "  and dat_saida is null "
        val mapa = HashMap<String, Any>()
        mapa["ordem_producao"] = op
        mapa["cod_usuario"] = codUsuario
        return jdbcTemplate.update(sql, mapa)
    }

    fun desmarcarTodos(ordensDeProducao: List<Int>, codUsuario: Int): List<Int> {
        val desmarcados = arrayListOf<Int>()
        ordensDeProducao.forEach { op ->
            println(op)
            if(permiteDesmarcarPrioridade(op)) {
                if(desmarcarPrioridade(op, codUsuario) > 0) {
                    desmarcados.add(op)
                }
            }
        }
        /* TODO verificar como usar update returning com jdbctemplate para nao precisar fazer o processo 1 por 1 */
        /*
            declare
            TYPE t_teste IS TABLE OF number;
            teste t_teste;
            begin
            update pacificosul.ps_tb_op_uti
            set sit_ativo = 0,
            dat_saida = sysdate,
            cod_usuario_saida = 7
            where ordem_producao in (100550, 100549, 100590, 100640, 97340, 11231)
            and dat_saida is null
            RETURNING ordem_producao BULK COLLECT INTO teste;
            end;
        */
        return desmarcados;
    }
}