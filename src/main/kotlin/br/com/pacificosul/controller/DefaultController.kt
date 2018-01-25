package br.com.pacificosul.controller

import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.JdbcTemplate

abstract class DefaultController {
    protected val oracleTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getOracleTemplate())
    protected val mysqlTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getMysqlTemplate())
}