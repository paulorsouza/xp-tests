package br.com.pacificosul.api

import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.jdbc.core.JdbcTemplate

abstract class DefaultApi {
    protected val oracleTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getOracleTemplate())
    protected val mysqlTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getMysqlTemplate())
}