package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class InsumoRepository: ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getEndereco(grupo: String, sub: String, item: String): String? {
        val sql = "select max(endereco) as endereco  from estq_110 a " +
                  "where a.nivel = '9' " +
                  "  and a.grupo = :grupo " +
                  "  and a.subgrupo = :sub " +
                  "  and a.item = :item " +
                  "  and a.deposito = 51 " +
                  "group by nivel,grupo "

        val mapa = HashMap<String, Any>()

        mapa["grupo"] = grupo
        mapa["sub"] = sub
        mapa["item"] = item
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString("enderecos")
        }.firstOrNull()
    }
}