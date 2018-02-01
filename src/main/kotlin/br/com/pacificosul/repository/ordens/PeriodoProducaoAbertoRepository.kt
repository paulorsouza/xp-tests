package br.com.pacificosul.repository.ordens

import br.com.pacificosul.data.ordens.PeriodoProducaoAbertoData
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PeriodoProducaoAbertoRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {
    fun get(estagiosConcatenados: List<Int>): List<PeriodoProducaoAbertoData> {

        val sql =   "select PERIODO_PRODUCAO, DES_PERIODO, count(DISTINCT ORDEM_PRODUCAO) as qtd_op," +
                    "sum(QTDE_EM_PRODUCAO_PACOTE) as QTDE_EM_PRODUCAO_PACOTE, decode(ORDENACAO,99,'',ordenacao) as ordenacao," +
                    "(PERIODO_PRODUCAO || ' - ' || DES_PERIODO) as cod_des_periodo " +
                    "from ( " +
                    "select a.PERIODO_PRODUCAO, c.DES_PERIODO, a.ORDEM_PRODUCAO, b.CODIGO_ESTAGIO, c.SIT_CONSIDERA_CONSULTA,decode(c.ORDENACAO,0,99,c.ORDENACAO) as ORDENACAO, " +
                    "(case " +
                    "when LAG(a.ORDEM_PRODUCAO, 1, 0) OVER (ORDER BY a.ORDEM_PRODUCAO,sum(QTDE_EM_PRODUCAO_PACOTE) desc) <> a.ORDEM_PRODUCAO then sum(QTDE_EM_PRODUCAO_PACOTE) else 0 end) " +
                    "as QTDE_EM_PRODUCAO_PACOTE " +
                    "from pcpc_020 a " +
                    "left join pcpc_040 b on (b.ORDEM_PRODUCAO = a.ORDEM_PRODUCAO) " +
                    "left join ps_tb_periodo_agrupado c on (c.PERIODO1 = a.PERIODO_PRODUCAO) " +
                    "where a.COD_CANCELAMENTO = :cancelamento and b.QTDE_EM_PRODUCAO_PACOTE > 0 " +
                    "and b.CODIGO_ESTAGIO in (:param) " +
                    "and a.PERIODO_PRODUCAO >= (select min(x.PERIODO) from pacificosul.PS_TB_PERIODO_TESTE x) " +
                    "group by a.PERIODO_PRODUCAO, c.DES_PERIODO, a.ORDEM_PRODUCAO, b.CODIGO_ESTAGIO, c.SIT_CONSIDERA_CONSULTA,c.ORDENACAO ) " +
                    "group by PERIODO_PRODUCAO, DES_PERIODO, SIT_CONSIDERA_CONSULTA,ORDENACAO " +
                    "order by ORDENACAO asc"

        val mapa = HashMap<String, Any>()
        mapa.set("param",estagiosConcatenados)
        mapa.set("cancelamento",0)

        return namedParameterJdbcTemplate.query(sql, mapa) {
            rs, _ -> PeriodoProducaoAbertoData(rs.getInt("PERIODO_PRODUCAO"),
                rs.getString("DES_PERIODO"), rs.getInt("QTDE_EM_PRODUCAO_PACOTE"), rs.getInt("qtd_op"))
        }.orEmpty()

    }
}