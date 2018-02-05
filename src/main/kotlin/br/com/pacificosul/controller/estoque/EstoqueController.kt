package br.com.pacificosul.controller.estoque

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.estoque.EstoqueDeposito
import br.com.pacificosul.model.Referencia
import br.com.pacificosul.repository.estoque.EstoqueRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/estoque")
class EstoqueController : DefaultController() {
    @GetMapping("/referencia/considera-tmrp/{isTmrp}")
    fun getEstoquePorDeposito(@RequestParam(name = "nivel", required = true) nivel: String,
                              @RequestParam(name = "grupo", required = true) grupo: String,
                              @RequestParam(name = "subgrupo", required = true) subgrupo: String,
                              @RequestParam(name = "item", required = true) item: String,
                              @PathVariable("isTmrp") isTmrp: Boolean = true): List<EstoqueDeposito> {
        val referencia = Referencia(nivel, grupo, subgrupo, item)
        return EstoqueRepository(oracleTemplate).getEstoquePorDeposito(referencia, isTmrp)
    }
}
