package br.com.pacificosul.controller.ordens

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.ordens.OrdemProducaoItem
import br.com.pacificosul.repository.ordens.OrdemProducaoRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/ordens")
class OrdemProducaoController: DefaultController() {
    @GetMapping("/itens")
    fun getOrdemItens(@RequestParam(name = "ordemProducao")ordemProducao: Int): List<OrdemProducaoItem> =
            OrdemProducaoRepository(oracleTemplate).getOrdemItens(ordemProducao)
    @GetMapping("{op}/estagios-paralelos")
    fun getEstagiosParalelos(@PathVariable("op") ordemProducao: Int,
                             @RequestParam(name = "grupo")grupo: String,
                             @RequestParam(name = "item")item: String) =
            OrdemProducaoRepository(oracleTemplate).getOndeTem(grupo, item)
}