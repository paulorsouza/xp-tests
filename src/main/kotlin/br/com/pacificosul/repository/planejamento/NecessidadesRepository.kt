package br.com.pacificosul.repository.planejamento

import br.com.pacificosul.data.planejamento.InsumoNecessidadeData
import br.com.pacificosul.model.Referencia
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class NecessidadesRepository() {
    fun insumoNecessidadeByOrdem(jdbcTemplate: JdbcTemplate, ordemProducao: Int): List<InsumoNecessidadeData> {
        val sql = "select consulta.* from "+
                "(select a.NIVEL_ESTRUTURA,a.GRUPO_ESTRUTURA,a.SUBGRU_ESTRUTURA,a.ITEM_ESTRUTURA, "+
                "(a.NIVEL_ESTRUTURA || '.' || a.GRUPO_ESTRUTURA || '.' || a.SUBGRU_ESTRUTURA || '.' ||a.ITEM_ESTRUTURA) codigo, "+
                "nvl((select sum(x.QTDE_ESTOQUE_ATU) from INTER_VI_ESTQ_040_ESTQ_080 x "+
                "where x.CDITEM_NIVEL99 = a.NIVEL_ESTRUTURA "+
                "and x.CDITEM_GRUPO = a.GRUPO_ESTRUTURA "+
                "and x.CDITEM_SUBGRUPO = a.SUBGRU_ESTRUTURA "+
                "and x.CDITEM_ITEM = a.ITEM_ESTRUTURA),0) as estoque_atual, "+
                "nvl((select sum(x.QTDE_ARECEBER) from tmrp_041 x "+
                "where x.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA "+
                "and x.GRUPO_ESTRUTURA = a.GRUPO_ESTRUTURA "+
                "and x.SUBGRU_ESTRUTURA = a.SUBGRU_ESTRUTURA "+
                "and x.ITEM_ESTRUTURA = a.ITEM_ESTRUTURA and x.periodo_producao > 0),0) as qtd_a_receber, "+
                "(select sum(x.QTDE_RESERVADA) from TMRP_041 x "+
                "where x.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA "+
                "and x.GRUPO_ESTRUTURA = a.GRUPO_ESTRUTURA "+
                "and x.SUBGRU_ESTRUTURA = a.SUBGRU_ESTRUTURA "+
                "and x.ITEM_ESTRUTURA = a.ITEM_ESTRUTURA and x.periodo_producao > 0) as QTDE_RESERVADA_GLOBAL, "+
                "(b.descr_referencia || ' / ' || c.descr_tam_refer || ' / ' || d.DESCRICAO_15) as descricao, "+
                "sum(a.QTDE_RESERVADA) as QTDE_RESERVADA, avg(a.CONSUMO) as consumo_medio from TMRP_041 a "+
                "left join basi_030 b on (b.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA and b.REFERENCIA = a.GRUPO_ESTRUTURA) "+
                "left join basi_010 d on (d.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA and d.GRUPO_ESTRUTURA = a.GRUPO_ESTRUTURA "+
                "                         and d.SUBGRU_ESTRUTURA = a.SUBGRU_ESTRUTURA and d.ITEM_ESTRUTURA = a.ITEM_ESTRUTURA) "+
                "left join basi_020 c on (c.BASI030_NIVEL030 = b.NIVEL_ESTRUTURA and c.BASI030_REFERENC = b.REFERENCIA and c.TAMANHO_REF = d.SUBGRU_ESTRUTURA) "+
                "where a.NR_PEDIDO_ORDEM = :ordemProducao "+
                "and a.periodo_producao > 0 and a.NIVEL_ESTRUTURA <> '1' "+
                "group by a.NIVEL_ESTRUTURA,a.GRUPO_ESTRUTURA,a.SUBGRU_ESTRUTURA,a.ITEM_ESTRUTURA,b.descr_referencia,c.descr_tam_refer,d.DESCRICAO_15 "+
                "order by a.NIVEL_ESTRUTURA "+
                ") consulta "

        var namedJdbc = NamedParameterJdbcTemplate(jdbcTemplate.dataSource)
        val mapa = HashMap<String, Any>()
        mapa.set("ordemProducao",ordemProducao)
        return namedJdbc.query(sql, mapa) {
            rs, _ -> InsumoNecessidadeData(Referencia(rs.getString("nivel_estrutura"), rs.getString("grupo_estrutura"),
                rs.getString("subgru_estrutura"), rs.getString("item_estrutura")), rs.getBigDecimal("estoque_atual"),
                rs.getBigDecimal("qtd_a_receber"), rs.getBigDecimal("qtde_reservada_global"), rs.getString("descricao"),
                rs.getBigDecimal("qtde_reservada"), rs.getBigDecimal("consumo_medio"))
        }
    }
}
