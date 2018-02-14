package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigDecimal

open class ProdutoRepository(val jdbcTemplate: NamedParameterJdbcTemplate) {
    open fun <T> getText(data: T) : String {
        return ""
    }

    fun getCorMostruario(grupo: String): String? {
        val sql = "select max(DISTINCT(y.PROCONF_ITEM)) as cor_mostruario from pcpc_020 x " +
                  "left join pcpc_040 y on (y.ORDEM_PRODUCAO = x.ORDEM_PRODUCAO) " +
                  "where x.REFERENCIA_PECA = :grupo " +
                  "  and x.TIPO_ORDEM = 2 and x.COD_CANCELAMENTO = 0"

        val mapa = HashMap<String, Any>()
        mapa["grupo"] = grupo

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString("cor_mostruario")
        }.firstOrNull()
    }

    fun getEstoqueTmrp(nivel: String, grupo: String, sub: String?, item: String?): Triple<BigDecimal, String, String> {
        val sql = StringBuilder()
        sql.append("select sum(a.QTDE_ESTOQUE_ATU) estoque_atual, b.UNIDADE_MEDIDA,wm_concat(DISTINCT a.deposito) as deposito_1, ")
        sql.append("case ")
        sql.append(" when sum(a.QTDE_ESTOQUE_ATU) > 0 ")
        sql.append("   then regexp_replace(listagg(a.DEPOSITO,',') within group (order by a.deposito),'([^,]*)(,\\1)+($|,)','\\1\\3') ")
        sql.append("   else '' ")
        sql.append("end as depositos ")
        sql.append("from INTER_VI_ESTQ_040_ESTQ_080 a ")
        sql.append("left join basi_030 b on (b.NIVEL_ESTRUTURA = a.CDITEM_NIVEL99 and b.REFERENCIA = a.CDITEM_GRUPO) ")
        sql.append("left join basi_205 c on (c.CODIGO_DEPOSITO = a.DEPOSITO) ")
        sql.append("where a.CDITEM_NIVEL99 = :nivel ")
        sql.append("and a.CDITEM_GRUPO = :grupo ")
        sql.append("and a.QTDE_ESTOQUE_ATU <> 0 and c.CONSIDERA_TMRP = 1 ")

        val mapa = HashMap<String, Any>()
        mapa["nivel"] = nivel
        mapa["grupo"] = grupo
        if(!sub.isNullOrBlank()) {
            sql.append("and a.CDITEM_SUBGRUPO = :sub ")
            mapa["sub"] = sub!!
        }
        if(!item.isNullOrBlank()) {
            sql.append("and a.CDITEM_ITEM = :item ")
            mapa["item"] = item!!
        }

        sql.append("group by b.UNIDADE_MEDIDA")

        return jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ -> Triple<BigDecimal, String, String>(
                rs.getBigDecimal("estoque_atual"),
                rs.getString("UNIDADE_MEDIDA"),
                rs.getString("depositos"))
        }.first()
    }

    fun getEstoque(nivel: String, grupo: String, sub: String?, item: String?): Triple<BigDecimal, String, String> {
        val sql = StringBuilder()
        sql.append("select sum(a.QTDE_ESTOQUE_ATU) estoque_atual, b.UNIDADE_MEDIDA,wm_concat(DISTINCT a.deposito) as deposito_1, ")
        sql.append("case ")
        sql.append(" when sum(a.QTDE_ESTOQUE_ATU) > 0 ")
        sql.append("   then regexp_replace(listagg(a.DEPOSITO,',') within group (order by a.deposito),'([^,]*)(,\\1)+($|,)','\\1\\3') ")
        sql.append("   else '' ")
        sql.append("end as depositos ")
        sql.append("from INTER_VI_ESTQ_040_ESTQ_080 a ")
        sql.append("left join basi_030 b on (b.NIVEL_ESTRUTURA = a.CDITEM_NIVEL99 and b.REFERENCIA = a.CDITEM_GRUPO) ")
        sql.append("where a.CDITEM_NIVEL99 = :nivel ")
        sql.append("and a.CDITEM_GRUPO = :grupo ")
        sql.append("and a.QTDE_ESTOQUE_ATU <> 0  ")

        val mapa = HashMap<String, Any>()
        mapa["nivel"] = nivel
        mapa["grupo"] = grupo
        if(!sub.isNullOrBlank()) {
            sql.append("and a.CDITEM_SUBGRUPO = :sub ")
            mapa["sub"] = sub!!
        }
        if(!item.isNullOrBlank()) {
            sql.append("and a.CDITEM_ITEM = :item ")
            mapa["item"] = item!!
        }

        sql.append("group by b.UNIDADE_MEDIDA")

        return jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ -> Triple<BigDecimal, String, String>(
                rs.getBigDecimal("estoque_atual"),
                rs.getString("UNIDADE_MEDIDA"),
                rs.getString("depositos"))
        }.first()
    }
}

