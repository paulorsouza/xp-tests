package br.com.pacificosul.controller

import br.com.pacificosul.data.ObservacaoData
import br.com.pacificosul.repository.ObservacaoRepository
import br.com.pacificosul.repository.UserRepository
import br.com.pacificosul.rules.getCodigoUsuario
import br.com.pacificosul.security.TokenClaims
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/observacao")
class ObservacaoController : DefaultController() {

    @GetMapping("/op/{numeroOp}")
    fun getObservacoes(
            @PathVariable("numeroOp") numeroOp: Int,
            @RequestParam(name = "todos", required = false) todos: Boolean = false) : List<ObservacaoData> {
        val rep = ObservacaoRepository(oracleTemplate)
        if(todos) {
            return rep.listAll(numeroOp)
        }
        return rep.listFromEstagiosParalelos(numeroOp)
    }

    @GetMapping("systextil/op/{numeroOp}")
    fun getObservacoeSystextil(
            @PathVariable("numeroOp") numeroOp: Int) : Pair<String, String>? {
        return ObservacaoRepository(oracleTemplate).getObservacaoSystextil(numeroOp)
    }

    @GetMapping("referencia/{numeroReferencia}")
    fun getObservacoeByRef(
            @PathVariable("numeroReferencia") numeroRef: Int) : List<ObservacaoData>? {
        return ObservacaoRepository(oracleTemplate).getObservacaoPeD(numeroRef)
    }

    @PostMapping("/op/{numeroOp}/add")
    fun addObservacao(authentication: Authentication,
                      @PathVariable("numeroOp") numeroOp: Int,
                      @RequestBody payload: Payload): ObservacaoData {
        val codUsuario = getCodigoUsuario(authentication)
        val nome = UserRepository(oracleTemplate).getNomeUsuario(codUsuario)
        ObservacaoRepository(oracleTemplate).addObservacao(nome.orEmpty(),
                numeroOp, payload.observacao.orEmpty(), payload.descEstagio.orEmpty())
        return ObservacaoData(numeroOp, nome, payload.observacao.orEmpty(), 1,
                payload.descEstagio.orEmpty(), Date.from(Instant.now()))
    }

    data class Payload(val descEstagio: String?, val observacao: String?)
}