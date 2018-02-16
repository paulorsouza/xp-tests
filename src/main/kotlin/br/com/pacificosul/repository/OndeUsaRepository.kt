package br.com.pacificosul.repository

import br.com.pacificosul.data.produto.CodigoProduto
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigDecimal

class OndeUsaRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun getOndeUsa(ckAgruparOndeUsa: Boolean, ckOndeUsaAlternativaPadrao: Boolean, codProduto: CodigoProduto): List<Unit> {
        val estoqueGlobal = BigDecimal.ZERO
        val sql = StringBuilder()
        val mapa = HashMap<String, Any>()
        mapa["nivel"] = codProduto.nivel.orEmpty()
        mapa["grupo"] = codProduto.grupo.orEmpty()

        if (ckAgruparOndeUsa) {
            sql.append("select NIVEL_ITEM,GRUPO_ITEM,UNIDADE_MEDIDA,ANO,INDICE_ESTACAO, ")
            sql.append("NIVEL_COMP,GRUPO_COMP,SUB_COMP,ITEM_COMP,DESCR_COLECAO,referencia, ")
            sql.append("ALTERNATIVA_COMP,ALTERNATIVA_ITEM,SUB_ITEM,ITEM_ITEM,sum(consumo) as consumo, ")
            sql.append("round(${estoqueGlobal}/sum(consumo)) as pecas_previstas ")
            sql.append("from( ")
        }

        sql.append("select a.NIVEL_ITEM,a.GRUPO_ITEM,basi_030.UNIDADE_MEDIDA, ")
        sql.append("a.NIVEL_COMP,a.GRUPO_COMP,a.SUB_COMP,a.ITEM_COMP,y.DESCR_COLECAO,ANO,INDICE_ESTACAO, ")
        sql.append("decode(NIVEL_ITEM, '1', ")
        sql.append("(case ")
        sql.append("when substr(a.GRUPO_ITEM, 1, 1) in ('A','B','C','D') then '2' || substr(a.GRUPO_ITEM, 2, 5) ")
        sql.append("else a.GRUPO_ITEM ")
        sql.append("end), '-') as referencia ")

        if (ckAgruparOndeUsa) {
            sql.append(",a.ALTERNATIVA_COMP,a.ALTERNATIVA_ITEM,'.' as SUB_ITEM,'.' as ITEM_ITEM, avg(a.CONSUMO) as consumo, ")
            sql.append("round(${estoqueGlobal}/avg(consumo)) as pecas_previstas ")
        } else {
            sql.append(",a.ALTERNATIVA_COMP,a.ALTERNATIVA_ITEM,a.SUB_ITEM,a.ITEM_ITEM, sum(a.CONSUMO) as consumo, ")
            sql.append("round(${estoqueGlobal}/sum(consumo)) as pecas_previstas ")
        }

        sql.append("from systextil.inter_vi_estrutura a ")
        sql.append("left join basi_030 on (basi_030.NIVEL_ESTRUTURA = a.NIVEL_COMP and basi_030.REFERENCIA = a.GRUPO_COMP) ")
        sql.append("left join basi_030 x on (x.NIVEL_ESTRUTURA = a.NIVEL_ITEM and x.REFERENCIA = a.GRUPO_ITEM) ")
        sql.append("left join basi_140 y on (y.COLECAO = x.colecao) ")
        sql.append("left join pacificosul.ps_vw_colecao z on (z.colecao = y.colecao) ")
        sql.append("where a.NIVEL_COMP = :nivel ")
        sql.append("  and a.GRUPO_COMP = :grupo ")

        if (codProduto.subGrupo != "000") {
            sql.append("  and a.SUB_COMP = :sub ")
            mapa["sub"] = codProduto.subGrupo.orEmpty()
        }

        if (codProduto.item != "000000") {
            sql.append("  and a.ITEM_COMP = :item ")
            mapa["item"] = codProduto.item.orEmpty()
        }

        if (ckAgruparOndeUsa) {
            sql.append("group by a.NIVEL_ITEM,a.GRUPO_ITEM, ")
            sql.append("a.NIVEL_COMP, a.GRUPO_COMP, a.SUB_COMP, a.ITEM_COMP, a.ALTERNATIVA_COMP, a.ALTERNATIVA_ITEM, ")
            sql.append("basi_030.UNIDADE_MEDIDA,y.DESCR_COLECAO,ANO,INDICE_ESTACAO) ")
        }

        if (ckOndeUsaAlternativaPadrao) {
            sql.append("where ALTERNATIVA_ITEM in (select x.NUMERO_ALTERNATI from basi_010 x ")
            sql.append("                           where x.NIVEL_ESTRUTURA = nivel_item and ")
            sql.append("                           x.GRUPO_ESTRUTURA = grupo_item) ")
        }

        if (ckAgruparOndeUsa) {
            sql.append("group by NIVEL_ITEM,GRUPO_ITEM,referencia, ")
            sql.append("NIVEL_COMP,GRUPO_COMP,SUB_COMP,ITEM_COMP, ")
            sql.append("UNIDADE_MEDIDA,DESCR_COLECAO, ")
            sql.append("ALTERNATIVA_COMP,ALTERNATIVA_ITEM,SUB_ITEM,ITEM_ITEM,ANO,INDICE_ESTACAO ")
            sql.append("ORDER BY ANO DESC,INDICE_ESTACAO ")
        } else {
            sql.append("group by NIVEL_ITEM,GRUPO_ITEM,basi_030.referencia, ")
            sql.append("NIVEL_COMP,GRUPO_COMP,SUB_COMP,ITEM_COMP, ")
            sql.append("basi_030.UNIDADE_MEDIDA,y.DESCR_COLECAO, ")
            sql.append("ALTERNATIVA_COMP,ALTERNATIVA_ITEM,SUB_ITEM,ITEM_ITEM,ANO,INDICE_ESTACAO ")
            sql.append("ORDER BY ANO DESC,INDICE_ESTACAO ")
        }

        println(sql.toString())

        return jdbcTemplate.query(sql.toString(), mapa){
            rs, _ -> println(rs)
        }.orEmpty()
    }
}