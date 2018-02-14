package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

open class TecidoRepository : ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)
}