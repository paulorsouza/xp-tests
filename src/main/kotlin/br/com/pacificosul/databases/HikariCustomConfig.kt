package br.com.pacificosul.databases

import com.zaxxer.hikari.HikariDataSource

class HikariCustomConfig {
    private val oracleTemplate: HikariDataSource
    private val mysqlTemplate: HikariDataSource

    init {
        oracleTemplate = oracleTemplate()
        mysqlTemplate = mysqlTemplate()
    }

    fun getOracleTemplate(): HikariDataSource = oracleTemplate
    fun getMysqlTemplate(): HikariDataSource = mysqlTemplate

    private fun oracleTemplate(): HikariDataSource {
        val hikariDS = HikariDataSource()
        hikariDS.setDriverClassName("oracle.jdbc.driver.OracleDriver")
        hikariDS.connectionTimeout = 5000
        hikariDS.maximumPoolSize = 5
        hikariDS.password = "oracle"
        hikariDS.username = "systextil"
        hikariDS.jdbcUrl = "jdbc:oracle:thin:@192.168.0.65:1521:tsh1"
        return hikariDS
    }

    private fun mysqlTemplate(): HikariDataSource {
        val hikariDS = HikariDataSource()
        hikariDS.connectionTimeout = 600
        hikariDS.maximumPoolSize = 5
        hikariDS.setDriverClassName("com.mysql.jdbc.Driver")
        hikariDS.password = "re2dzj63"
        hikariDS.username = "root"
        hikariDS.jdbcUrl = "jdbc:mysql://192.168.0.48:3306/vendor_db"
        return hikariDS
    }
}