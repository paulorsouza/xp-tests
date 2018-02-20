package br.com.pacificosul.repository

import br.com.pacificosul.data.LogPrioridadeOpData
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

    fun logPrioridade(numeroOp: Int): List<LogPrioridadeOpData> {
        val sql = "select uti.dat_entrada, usuario_entrada.des_apelido as usuario_entrada, " +
                "       uti.dat_saida, usuario_saida.des_apelido as usuario_saida " +
                "from pacificosul.ps_tb_op_uti uti, " +
                "     pacificosul.ps_tb_usuario usuario_entrada, " +
                "     pacificosul.ps_tb_usuario usuario_saida " +
                "where uti.cod_usuario_entrada = usuario_entrada.cod_usuario (+) " +
                "  and uti.cod_usuario_saida = usuario_saida.cod_usuario (+) " +
                "  and uti.ordem_producao = :numeroOp " +
                "order by uti.dat_entrada desc, uti.dat_saida desc"
        val mapa = HashMap<String, Any>()
        mapa["numeroOp"] = numeroOp
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> LogPrioridadeOpData(rs.getDate("dat_entrada"), rs.getString("usuario_entrada"),
                rs.getDate("dat_saida"), rs.getString("usuario_saida"))
        }.orEmpty()
    }
}