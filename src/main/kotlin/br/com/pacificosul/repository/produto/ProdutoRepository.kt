package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

open class ProdutoRepository(val jdbcTemplate: NamedParameterJdbcTemplate) {
}
