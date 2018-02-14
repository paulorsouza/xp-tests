package br.com.pacificosul.controller

import br.com.pacificosul.data.produto.LocalizadorData
import br.com.pacificosul.data.produto.LocalizadorResultData
import br.com.pacificosul.data.produto.PecaData
import br.com.pacificosul.repository.produto.LocalizadorRepository
import br.com.pacificosul.repository.produto.PecaRepository
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/produto")
class ProdutoController : DefaultController() {

    @PostMapping("/localizarProdutos")
    fun localizarProdutos(@RequestBody payload: LocalizadorData): List<LocalizadorResultData> {
        val rep = LocalizadorRepository(oracleTemplate)
        return rep.listProdutos(payload)
    }

    @GetMapping("{referencia}/texto")
    fun getText(@PathVariable("referencia") referencia: String): String {
        val rep = PecaRepository(oracleTemplate)
        val data = rep.getPecaData(referencia)
        return rep.getText(data)
    }

}