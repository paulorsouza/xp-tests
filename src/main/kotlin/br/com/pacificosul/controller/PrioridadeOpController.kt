package br.com.pacificosul.controller

import br.com.pacificosul.data.LogPrioridadeOpData
import br.com.pacificosul.repository.PrioridadeOpRepository
import br.com.pacificosul.rules.getCodigoUsuario
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/prioridadeOp")
class PrioridadeOpController : DefaultController() {
    @GetMapping("/op/{numeroOp}/log")
    fun getLogUti(@PathVariable("numeroOp")numeroOp: Int): List<LogPrioridadeOpData> =
            PrioridadeOpRepository(oracleTemplate).logPrioridade(numeroOp)

    @PostMapping("/op/{numeroOp}/grupo/{grupo}/marcar")
    fun marcarUti(authentication: Authentication,
                  @PathVariable("numeroOp") numeroOp: Int,
                  @PathVariable("grupo") grupo: String): ResponseEntity<Any> {
        val codUsuario = getCodigoUsuario(authentication)

        val rep = PrioridadeOpRepository(oracleTemplate)
        if (rep.temPrioridade(numeroOp)) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        rep.marcarPrioridade(numeroOp, grupo, codUsuario)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/op/{numeroOp}/desmarcar")
    fun desmarcarUti(authentication: Authentication,
                     @PathVariable("numeroOp") numeroOp: Int): ResponseEntity<Any> {
        val codUsuario = getCodigoUsuario(authentication)
        val rep = PrioridadeOpRepository(oracleTemplate)
        if (!rep.permiteDesmarcarPrioridade(numeroOp)) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        rep.desmarcarPrioridade(numeroOp, codUsuario)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/desmarcarTodos")
    fun desmarcarTodosUti(authentication: Authentication,
                          @RequestBody payload: List<Int>): List<Int> {
        val codUsuario = getCodigoUsuario(authentication)
        val rep = PrioridadeOpRepository(oracleTemplate)
        return rep.desmarcarTodos(payload, codUsuario)
    }
}