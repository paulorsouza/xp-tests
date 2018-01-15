package br.com.pacificosul.rules

import br.com.pacificosul.repository.getDescricaoPeriodo
import org.springframework.jdbc.core.JdbcTemplate

class Periodo {
    fun getDescricaoPeriodoInLine(jdbcTemplate: JdbcTemplate, codigoPeriodo: Int): String {
        val periodosDescricao = getDescricaoPeriodo(jdbcTemplate, codigoPeriodo)
        return periodosDescricao.joinToString(", ")
    }
}
