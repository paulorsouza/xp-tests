package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

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
}

