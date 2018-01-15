package br.com.pacificosul

import br.com.pacificosul.api.DefaultApi
import br.com.pacificosul.data.Obj
import br.com.pacificosul.data.TotalPedidosRecebidosData
import br.com.pacificosul.databases.HikariCustomConfig
import br.com.pacificosul.rules.sayHello
import br.com.pacificosul.rules.totalPedidosRecebidos
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.util.concurrent.atomic.AtomicLong
@RestController
@RequestMapping("/hello")
class HelloController: DefaultApi() {
    private val atomicLong = AtomicLong()

    @GetMapping("/")
    fun hello() = "Hello World."

    @GetMapping("/name/{name}")
    fun hello(@PathVariable(name = "name") name: String): String = "Hello $name. " +
            "Called: ${atomicLong.incrementAndGet()} times and this works fine."

    @GetMapping("/mysql")
    fun helloMysql(): Obj = sayHello(mysqlTemplate)

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.128:3000"))
    @GetMapping("/mysql/pedidosRecebidos/datafiltro/{dataFiltro}")
    fun getPedidoRecebidos(@PathVariable(name = "dataFiltro") dataFiltro: String): List<TotalPedidosRecebidosData> =
            totalPedidosRecebidos(mysqlTemplate, Date.valueOf(dataFiltro), false, false)

    @GetMapping("/oracle")
    fun bar(): Obj {
        val obj = oracleTemplate.query("select 'Fabio' as nome from dual") {
            rs, _ ->
            Obj(rs.getString("nome"))
        }.single()
        return obj
    }
}

