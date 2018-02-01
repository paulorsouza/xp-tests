package br.com.pacificosul.controller.estoque

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.model.Referencia
import br.com.pacificosul.repository.estoque.EstoqueRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/estoque")
class EstoqueController : DefaultController() {
    @GetMapping("/referencia/{referencia}/cosnidera-tmrp/{isTmrp}")
    fun getEstoquePorDeposito(@PathVariable("referencia") referencia: Referencia,@PathVariable("isTmrp") isTmrp: Boolean = true) = EstoqueRepository(oracleTemplate).getEstoquePorDeposito(referencia, isTmrp)
}