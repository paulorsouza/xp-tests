package br.com.pacificosul.api.planejamento

import br.com.pacificosul.api.DefaultApi
import br.com.pacificosul.data.planejamento.InsumoNecessidadeData
import br.com.pacificosul.repository.planejamento.NecessidadesRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/planejamento")
class NecessidadesApi : DefaultApi() {
    @GetMapping("/insumosNecessidade")
    fun getInsumosNecessidadeByOrdemProducao(
            @RequestParam(name="ordemProducao",required = true)ordemProducao: Int)
            : List<InsumoNecessidadeData> = NecessidadesRepository()
                .insumoNecessidadeByOrdem(oracleTemplate, ordemProducao)
}