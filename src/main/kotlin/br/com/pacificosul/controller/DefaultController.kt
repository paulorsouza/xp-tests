package br.com.pacificosul.controller

import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

abstract class DefaultController {
    protected val  oracleTemplate: NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(HikariCustomConfig().getOracleTemplate())
    protected val oracleTemplateNonNamed: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getOracleTemplate())
    protected val mysqlTemplate: NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(HikariCustomConfig().getMysqlTemplate())
}