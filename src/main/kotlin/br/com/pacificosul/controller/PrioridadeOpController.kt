package br.com.pacificosul.controller

import br.com.pacificosul.repository.PrioridadeOpRepository
import br.com.pacificosul.security.TokenClaims
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/prioridadeOp")
class PrioridadeOpController : DefaultController() {

    @PostMapping("/op/{numeroOp}/grupo/{grupo}/marcar")
    fun marcarUti(authentication: Authentication,
                  @PathVariable("numeroOp") numeroOp: Int,
                  @PathVariable("grupo") grupo: String): ResponseEntity<Any> {
        val claims = authentication.principal as TokenClaims
        val codUsuario = Integer.parseInt(claims.cod_usuario)

        val rep = PrioridadeOpRepository(oracleTemplate)
        if (rep.temPrioridade(numeroOp)) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        rep.marcarPrioridade(numeroOp, grupo, codUsuario)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/op/{numeroOp}/desmarcar")
    fun desmarcarUti(authentication: Authentication,
                     @PathVariable("numeroOp") numeroOp: Int): ResponseEntity<Any> {
        val claims = authentication.principal as TokenClaims
        val codUsuario = Integer.parseInt(claims.cod_usuario)

        val rep = PrioridadeOpRepository(oracleTemplate)
        if (rep.permiteDesmarcarPrioridade(numeroOp)) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        rep.desmarcarPrioridade(numeroOp, codUsuario)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/desmarcarTodos")
    fun desmarcarTodosUti(authentication: Authentication,
                          @RequestBody payload: List<String>): ResponseEntity<Any> {
        val claims = authentication.principal as TokenClaims
        val codUsuario = Integer.parseInt(claims.cod_usuario)
        return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}