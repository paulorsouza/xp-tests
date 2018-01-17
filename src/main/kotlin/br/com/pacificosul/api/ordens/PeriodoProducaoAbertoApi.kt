package br.com.pacificosul.api.ordens

import br.com.pacificosul.api.DefaultApi
import br.com.pacificosul.data.ordens.PeriodoProducaoAbertoData
import br.com.pacificosul.repository.ordens.PeriodoProducaoAbertoRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/periodo-producao")
class PeriodoProducaoAbertoApi : DefaultApi() {
    @GetMapping("/teste")
    fun getPeriodoProducaoAberto(@RequestParam(name="listaEstagios", defaultValue = "",required = false) estagios: List<Int>): List<PeriodoProducaoAbertoData> {
        return PeriodoProducaoAbertoRepository(oracleTemplate).get(estagios)
    }
}