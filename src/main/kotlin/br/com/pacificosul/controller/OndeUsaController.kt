package br.com.pacificosul.controller

import br.com.pacificosul.data.produto.CodigoProduto
import br.com.pacificosul.repository.OndeUsaRepository
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/ondeUsa")
class OndeUsaController : DefaultController() {
    @GetMapping("/")
    fun getOndeUsa() : List<Unit> {
        val cod = CodigoProduto("1", "11111", "000", "000000")
        return OndeUsaRepository(oracleTemplate).getOndeUsa(true, true, cod)
    }
}