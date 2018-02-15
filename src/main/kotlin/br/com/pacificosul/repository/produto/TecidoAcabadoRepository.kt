package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class TecidoAcabadoRepository: TecidoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)
}