package br.com.pacificosul.databases

import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class JdbcTemplateOracle() : JdbcTemplate()
class JdbcTemplateMysql: JdbcTemplate(HikariCustomConfig().getMysqlTemplate())