package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PecaRepository: ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getEndereco(referencia: String): String? {
        val sql = "select wm_concat(DISTINCT endereco) as enderecos " +
                  "from estq_110 " +
                  "where estq_110.NIVEL = '1' " +
                  "  and estq_110.GRUPO = :referencia " +
                  "  and deposito in (1,65) " +
                  "group by nivel,grupo"

        val mapa = HashMap<String, Any>()
        mapa["referencia"] = referencia

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString("enderecos")
        }.firstOrNull()
    }
}