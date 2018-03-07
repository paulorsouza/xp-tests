package br.com.pacificosul.controller.planejamento

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.planejamento.RolosOrdemData
import br.com.pacificosul.model.Referencia
import br.com.pacificosul.repository.planejamento.PlanejamentoRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/planejamento")
class PlanejamentoController : DefaultController() {
    @GetMapping("/produto/rolos-empenhados")
    fun getRolosEmpenhadosPorDeposito(@RequestParam(name = "nivel", required = true) nivel: String,
                      @RequestParam(name = "grupo", required = true) grupo: String,
                      @RequestParam(name = "subgrupo", required = true) subgrupo: String,
                      @RequestParam(name = "item", required = true) item: String,
                      @RequestParam(name = "deposito", required = true) deposito: Int): List<RolosOrdemData> {
        val referencia = Referencia(nivel, grupo, subgrupo, item)
        return PlanejamentoRepository(oracleTemplate).rolosOrdem(referencia, deposito)
    }
}
