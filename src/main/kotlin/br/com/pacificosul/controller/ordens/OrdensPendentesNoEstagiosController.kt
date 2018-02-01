package br.com.pacificosul.controller.ordens

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.ordens.OrdensAProduzirData
import br.com.pacificosul.repository.ordens.OrdensAProduzirRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/ordem-pendente-estagio")
class OrdensPendentesNoEstagiosController : DefaultController() {
    @GetMapping("/periodo-estagio")
    fun getPentendeEstagioPorPeriodoEstagio(@RequestParam(name="listaPeriodos", defaultValue = "",required = false) periodos: List<Int>,
                                            @RequestParam(name="listaEstagios", defaultValue = "", required = false) estagios: List<Int>): List<OrdensAProduzirData> {
        return OrdensAProduzirRepository(oracleTemplate).get(periodos,estagios)
    }
}