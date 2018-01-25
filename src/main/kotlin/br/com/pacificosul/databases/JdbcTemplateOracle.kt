package br.com.pacificosul.databases

import org.springframework.jdbc.core.JdbcTemplate

class JdbcTemplateOracle : JdbcTemplate()
class JdbcTemplateMysql: JdbcTemplate(HikariCustomConfig().getMysqlTemplate())