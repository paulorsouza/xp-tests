package br.com.pacificosul.controller.ordens

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.LocalizadorResultData
import br.com.pacificosul.data.ordens.OrdemCancelamentoPayload
import br.com.pacificosul.data.ordens.OrdemFilhasData
import br.com.pacificosul.data.ordens.OrdemProducaoItem
import br.com.pacificosul.repository.ordens.OrdemProducaoRepository
import br.com.pacificosul.rules.cancelarOrdemProducao
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.http.ResponseEntity
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
                             @RequestParam(name = "grupo")grupo: String) =
            OrdemProducaoRepository(oracleTemplate).getOndeTem(grupo)
    @GetMapping("/filhas")
    fun getOrdemFilhas(@RequestParam(name = "ordemPrincipal")ordemPrincipal: Int): List<OrdemFilhasData> =
            OrdemProducaoRepository(oracleTemplate).getOrdemFilhas((ordemPrincipal))
    @PostMapping("/{op}/cancelar")
    fun cancelar(@PathVariable(name = "op")ordemProducao: Int,
                 @RequestBody payload: OrdemCancelamentoPayload): ResponseEntity<Any> {
        val r = cancelarOrdemProducao(oracleTemplate, ordemProducao, payload.observacao)
        return ResponseEntity(r, if (r.hasErrors) HttpStatus.UNPROCESSABLE_ENTITY else HttpStatus.OK)
    }
    @GetMapping("/foo")
    fun foo(): List<LocalizadorResultData> = OrdemProducaoRepository(oracleTemplate).getFoo()
}