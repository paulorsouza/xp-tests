package br.com.pacificosul.api.ordens

import br.com.pacificosul.api.DefaultApi
import br.com.pacificosul.data.ordens.PeriodoProducaoAbertoData
import br.com.pacificosul.repository.ordens.PeriodoProducaoAbertoRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/periodo-producao")
class PeriodoProducaoAbertoApi : DefaultApi() {
    @GetMapping("/teste")
    fun getPeriodoProducaoAberto(@RequestParam(name="listaEstagios", defaultValue = "",required = false) estagios: List<Int>): List<PeriodoProducaoAbertoData> {
        return PeriodoProducaoAbertoRepository(oracleTemplate).get(estagios)
    }
}