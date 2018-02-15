package br.com.pacificosul.repository.produto

import br.com.pacificosul.data.produto.ProdutoData
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

        val result = jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ -> Triple<BigDecimal, String, String>(
                rs.getBigDecimal("estoque_atual"),
                rs.getString("UNIDADE_MEDIDA"),
                rs.getString("depositos"))
        }.firstOrNull()

        if(result == null) {
            return Triple(BigDecimal.ZERO, "", "")
        }
        return result
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

        val result = jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ -> Triple<BigDecimal, String, String>(
                rs.getBigDecimal("estoque_atual"),
                rs.getString("UNIDADE_MEDIDA"),
                rs.getString("depositos"))
        }.firstOrNull()

        if(result == null) {
            return Triple(BigDecimal.ZERO, "", "")
        }
        return result
    }

    fun getEstoqueReservado(nivel: String, grupo: String, sub: String, item: String) : Pair<BigDecimal?, BigDecimal?> {
        val sql = "" +
                "select sum(x.QTDE_ARECEBER) as QTDE_ARECEBER, sum(x.QTDE_RESERVADA) as QTDE_RESERVADA_GLOBAL " +
                "from tmrp_041 x " +
                "where x.NIVEL_ESTRUTURA = :nivel " +
                "  and x.GRUPO_ESTRUTURA = :grupo " +
                "  and x.SUBGRU_ESTRUTURA = :sub" +
                "  and x.ITEM_ESTRUTURA = :item " +
                "  and x.periodo_producao > 0 "
        val mapa = HashMap<String, Any>()
        mapa["nivel"] = nivel
        mapa["grupo"] = grupo
        mapa["sub"] = sub
        mapa["item"] = item

        val result = jdbcTemplate.query(sql.toString(), mapa) {
            rs, _ -> Pair<BigDecimal?, BigDecimal?>(
                rs.getBigDecimal("QTDE_ARECEBER"),
                rs.getBigDecimal("QTDE_RESERVADA_GLOBAL"))
        }.firstOrNull()

        if(result == null) {
            return Pair(BigDecimal.ZERO, BigDecimal.ZERO)
        }
        return result
    }

    fun getProdutoDataSql(nivel: String, grupo: String, sub: String, item: String) : Pair<String, HashMap<String, Any>> {
        val sql = "" +
                "select (a.descr_referencia || ' / ' || g.descr_tam_refer || ' / ' || f.DESCRICAO_15) as descricao, " +
                "g.RENDIMENTO,a.UNIDADE_MEDIDA,g.GRAMATURA_1, g.LARGURA_1, d.DESCR_ARTIGO as artigo_cota, i.DESCR_CT_ESTOQUE, f.COMPLEMENTO from basi_010 f " +
                "left join basi_030 a on (a.NIVEL_ESTRUTURA = f.NIVEL_ESTRUTURA and a.REFERENCIA = f.GRUPO_ESTRUTURA) " +
                "left join basi_020 g on (g.BASI030_NIVEL030 = a.NIVEL_ESTRUTURA and g.BASI030_REFERENC = a.REFERENCIA and g.TAMANHO_REF = f.SUBGRU_ESTRUTURA) " +
                "left join basi_140 b on (b.COLECAO = a.COLECAO) " +
                "left join basi_290 c on (c.ARTIGO = a.ARTIGO) " +
                "left join basi_295 d on (d.ARTIGO_COTAS = a.ARTIGO_COTAS) " +
                "left join basi_210 e on (e.SERIE_TAMANHO = a.SERIE_TAMANHO) " +
                "left join basi_150 i on (i.conta_estoque = a.conta_estoque) " +
                "where f.NIVEL_ESTRUTURA = :nivel " +
                "and f.GRUPO_ESTRUTURA = :grupo " +
                "and f.SUBGRU_ESTRUTURA = :sub " +
                "and f.ITEM_ESTRUTURA = :item "
        val mapa = HashMap<String, Any>()
        mapa["nivel"] = nivel
        mapa["grupo"] = grupo
        mapa["sub"] = sub
        mapa["item"] = item
        return Pair(sql.toString(), mapa)
    }

}

