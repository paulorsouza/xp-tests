package br.com.pacificosul.controller.planejamento

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.planejamento.InsumoNecessidadeData
import br.com.pacificosul.repository.planejamento.NecessidadesRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/planejamento")
class NecessidadesApi : DefaultController() {
    @GetMapping("/insumosNecessidade")
    fun getInsumosNecessidadeByOrdemProducao(
            @RequestParam(name="ordemProducao",required = true)ordemProducao: Int)
            : List<InsumoNecessidadeData> = NecessidadesRepository()
                .insumoNecessidadeByOrdem(oracleTemplate, ordemProducao)
}