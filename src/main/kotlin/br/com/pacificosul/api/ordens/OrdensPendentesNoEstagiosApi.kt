package br.com.pacificosul.api.ordens

import br.com.pacificosul.api.DefaultApi
import br.com.pacificosul.data.ordens.OrdensAProduzirData
import br.com.pacificosul.data.ordens.PeriodoProducaoAbertoData
import br.com.pacificosul.repository.ordens.OrdensAProduzirRepository
import br.com.pacificosul.repository.ordens.PeriodoProducaoAbertoRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/ordem-pendente-estagio")
class OrdensPendentesNoEstagiosApi : DefaultApi() {
    @GetMapping("/periodo-estagio")
    fun getPentendeEstagioPorPeriodoEstagio(@RequestParam(name="listaPeriodos", defaultValue = "",required = false) periodos: List<Int>,
                                            @RequestParam(name="listaEstagios", defaultValue = "", required = false) estagios: List<Int>): List<OrdensAProduzirData> {
        return OrdensAProduzirRepository(oracleTemplate).get(periodos,estagios)
    }

    /*@GetMapping("/produto")
    fun getPentendeEstagioPorProduto(@RequestParam(name="produto", defaultValue = "",required = false) produto: List<Int>): List<PeriodoProducaoAbertoData> {
        return PeriodoProducaoAbertoRepository(oracleTemplate).get(estagios)
    }

    @GetMapping("/ordem-producao")
    fun getPentendeEstagioPorOrdemProducao(@RequestParam(name="listaEstagios", defaultValue = "",required = false) estagios: List<Int>): List<PeriodoProducaoAbertoData> {
        return PeriodoProducaoAbertoRepository(oracleTemplate).get(estagios)
    }*/
}