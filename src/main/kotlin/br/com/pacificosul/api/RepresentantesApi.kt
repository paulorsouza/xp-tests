package br.com.pacificosul.api

import br.com.pacificosul.data.PedidosRecebidosData
import br.com.pacificosul.data.TotalPedidosRecebidosData
import br.com.pacificosul.rules.pedidosRecebidos
import br.com.pacificosul.rules.totalPedidosRecebidos
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/representantes")
class RepresentantesApi: DefaultApi() {

    @GetMapping("/mysql/totalPedidosRecebidos")
    fun getTotalPedidosRecebidos(@RequestParam(name = "dataFiltro", required = true) dataFiltro: String,
                                 @RequestParam(name = "mostrarPedidos") mostrarPedidos: Boolean = false,
                                 @RequestParam(name = "agrupar") agrupar: Boolean = true): List<TotalPedidosRecebidosData> {
        val stringPattern = "dd-MM-yyyy"
        val dtf = DateTimeFormatter.ofPattern(stringPattern)
        val parsedData = LocalDate.parse(dataFiltro, dtf)
        return totalPedidosRecebidos(mysqlTemplate, Date.valueOf(parsedData), mostrarPedidos, agrupar)
    }

    @GetMapping("/mysql/pedidosRecebidos", headers = arrayOf("Accept=application/json"))
    fun getPedidosRecebidos(@RequestParam(name = "periodo", required = false) periodo: String? = null,
                            @RequestParam(name = "codRepresentante", required = false) codRepresentante: Int? = null,
                            @RequestParam(name = "dataInicioFiltro", required = false) dataInicioFiltro: String? = null,
                            @RequestParam(name = "dataTerminoFiltro", required = false) dataTerminoFiltro: String? = null): List<PedidosRecebidosData> {

        val stringPattern = "dd-MM-yyyy"
        val dtf = DateTimeFormatter.ofPattern(stringPattern)
        val parsedDataInicio = Date.valueOf(LocalDate.parse(dataInicioFiltro, dtf))
        val parsedDataTermino = if (dataTerminoFiltro != null) Date.valueOf(LocalDate.parse(dataTerminoFiltro, dtf)) else null
        return pedidosRecebidos(mysqlTemplate, periodo, codRepresentante, parsedDataInicio, parsedDataTermino)
    }
}
