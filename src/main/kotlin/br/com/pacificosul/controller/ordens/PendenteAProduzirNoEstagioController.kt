package br.com.pacificosul.controller.ordens

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.ordens.EstagiosAProduzir200Data
import br.com.pacificosul.repository.ordens.PendenteEstagioRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/pendente-aproduzir")
class PendenteAProduzirNoEstagioController : DefaultController() {
    @GetMapping("/estagios-abertos/{tela}")
    fun getEstagiosAbertos200(@PathVariable(value = "", required = false) tela: String,
                              @RequestParam(name = "periodos", value = "", required = false) periodos: String?): List<EstagiosAProduzir200Data> {
        return PendenteEstagioRepository(oracleTemplate).get(periodos.orEmpty())
    }
}