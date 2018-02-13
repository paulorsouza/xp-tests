package br.com.pacificosul.controller

import br.com.pacificosul.data.LocalizadorData
import br.com.pacificosul.data.LocalizadorResultData
import br.com.pacificosul.repository.ProdutoRepository
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/produto")
class ProdutoController : DefaultController() {

    @PostMapping("/localizarProdutos")
    fun localizarProdutos(authentication: Authentication,
                          @RequestBody payload: LocalizadorData): List<LocalizadorResultData> {
        val rep = ProdutoRepository(oracleTemplate)
        return rep.listProdutos(payload)
    }

}