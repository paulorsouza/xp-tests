package br.com.pacificosul.repository.produto

import br.com.pacificosul.data.produto.LocalizadorData
import br.com.pacificosul.data.produto.LocalizadorResultData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class LocalizadorRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun listProdutos(params: LocalizadorData): List<LocalizadorResultData> {
        val sql = StringBuilder()
        sql.append("SELECT ")
        sql.append("  con.* ")
        sql.append("FROM (SELECT ")
        sql.append("  a.NIVEL_ESTRUTURA, ")
        sql.append("  a.GRUPO_ESTRUTURA, ")
        sql.append("  a.SUBGRU_ESTRUTURA, ")
        sql.append("  a.ITEM_ESTRUTURA, ")
        sql.append("  PS_FN_GET_DESCRICAO_PRODUTO(a.NIVEL_ESTRUTURA, a.GRUPO_ESTRUTURA, a.SUBGRU_ESTRUTURA, ")
        sql.append("  a.ITEM_ESTRUTURA) AS descricao, ")
        sql.append("  a.COMPLEMENTO, ")
        sql.append("  b.UNIDADE_MEDIDA, ")
        sql.append("  nvl((SELECT ")
        sql.append("    ROUND(SUM(x.QTDE_ARECEBER), 2) ")
        sql.append("  FROM tmrp_041 x ")
        sql.append("  WHERE x.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA ")
        sql.append("  AND x.GRUPO_ESTRUTURA = a.GRUPO_ESTRUTURA ")
        sql.append("  AND x.SUBGRU_ESTRUTURA = a.SUBGRU_ESTRUTURA ")
        sql.append("  AND x.ITEM_ESTRUTURA = a.ITEM_ESTRUTURA ")
        sql.append("  AND x.PERIODO_PRODUCAO > 0), 0) AS qtde_areceber, ")
        sql.append("  nvl((SELECT ")
        sql.append("    ROUND(SUM(y.QTDE_RESERVADA), 2) ")
        sql.append("  FROM tmrp_041 y ")
        sql.append("  WHERE y.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA ")
        sql.append("  AND y.GRUPO_ESTRUTURA = a.GRUPO_ESTRUTURA ")
        sql.append("  AND y.SUBGRU_ESTRUTURA = a.SUBGRU_ESTRUTURA ")
        sql.append("  AND y.ITEM_ESTRUTURA = a.ITEM_ESTRUTURA ")
        sql.append("  AND y.PERIODO_PRODUCAO > 0), 0) AS qtde_reservado, ")
        sql.append("  nvl((SELECT ")
        sql.append("    ROUND(SUM(z.QTDE_ESTOQUE_ATU), 2) ")
        sql.append("  FROM INTER_VI_ESTQ_040_ESTQ_080 z ")
        sql.append("  LEFT JOIN basi_205 zz ")
        sql.append("    ON (zz.CODIGO_DEPOSITO = z.DEPOSITO) ")
        sql.append("  WHERE zz.CONSIDERA_TMRP = 1 ")
        sql.append("  AND z.CDITEM_NIVEL99 = a.NIVEL_ESTRUTURA ")
        sql.append("  AND z.CDITEM_GRUPO = a.GRUPO_ESTRUTURA ")
        sql.append("  AND z.CDITEM_SUBGRUPO = a.SUBGRU_ESTRUTURA ")
        sql.append("  AND z.CDITEM_ITEM = a.ITEM_ESTRUTURA), 0) AS qtde_estq_tmrp, ")
        sql.append("  nvl((SELECT ")
        sql.append("    ROUND(SUM(z.QTDE_ESTOQUE_ATU), 2) ")
        sql.append("  FROM INTER_VI_ESTQ_040_ESTQ_080 z ")
        sql.append("  WHERE z.CDITEM_NIVEL99 = a.NIVEL_ESTRUTURA ")
        sql.append("  AND z.CDITEM_GRUPO = a.GRUPO_ESTRUTURA ")
        sql.append("  AND z.CDITEM_SUBGRUPO = a.SUBGRU_ESTRUTURA ")
        sql.append("  AND z.CDITEM_ITEM = a.ITEM_ESTRUTURA), 0) AS qtde_estq_global ")
        sql.append("FROM basi_010 a ")
        sql.append("LEFT JOIN basi_030 b ")
        sql.append("  ON (b.NIVEL_ESTRUTURA = a.NIVEL_ESTRUTURA ")
        sql.append("  AND b.REFERENCIA = a.GRUPO_ESTRUTURA)) con ")
        sql.append("WHERE con.nivel_estrutura <> '1'")

        val mapa = HashMap<String, Any>()

        if(!params.nivel.isNullOrBlank()) {
            sql.append(" and con.NIVEL_ESTRUTURA = :nivel")
            mapa.put("nivel", params.nivel!!.toUpperCase())
        }

        if(!params.grupo.isNullOrBlank()) {
            sql.append(" and con.GRUPO_ESTRUTURA = :grupo")
            mapa.put("grupo", params.grupo!!.toUpperCase())
        }

        if(!params.subGrupo.isNullOrBlank()) {
            sql.append(" and con.SUBGRU_ESTRUTURA = :sub")
            mapa.put("sub", params.subGrupo!!.toUpperCase())
        }

        if(!params.item.isNullOrBlank()) {
            sql.append(" and con.ITEM_ESTRUTURA = :item")
            mapa.put("item", params.item!!.toUpperCase())
        }

        if(!params.complemento.isNullOrBlank()) {
            sql.append(" and con.COMPLEMENTO like :complemento")
            mapa.put("complemento", "%" + params.complemento!!.toUpperCase() + "%")
        }

        if(!params.descricao.isNullOrBlank()) {
            sql.append(" and  con.descricao like :descricao")
            mapa.put("descricao", "%" + params.descricao!!.toUpperCase() + "%")
        }

        sql.append(" order by con.QTDE_ESTQ_GLOBAL desc ")

        return jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ ->
            LocalizadorResultData(rs.getString("nivel_estrutura"),
                    rs.getString("grupo_estrutura"), rs.getString("subgru_estrutura"), rs.getString("item_estrutura"),
                    rs.getString("descricao"), rs.getString("complemento"), rs.getInt("qtde_areceber"),
                    rs.getInt("qtde_reservado"), rs.getInt("qtde_estq_tmrp"), rs.getInt("qtde_estq_global"))
        }.orEmpty()
    }
}