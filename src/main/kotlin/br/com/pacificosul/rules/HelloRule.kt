package br.com.pacificosul.rules

import br.com.pacificosul.data.Obj
import org.springframework.jdbc.core.JdbcTemplate

fun sayHello(jdbcTemplate: JdbcTemplate): Obj {
    return jdbcTemplate.query("select 'Denis' as nome from rep_pedido limit 1") {
        rs, _ ->
        Obj(rs.getString("nome"))
    }.single<Obj>()
}