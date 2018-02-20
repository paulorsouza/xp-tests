package br.com.pacificosul.controller

import br.com.pacificosul.data.produto.LocalizadorData
import br.com.pacificosul.data.produto.LocalizadorResultData
import br.com.pacificosul.repository.produto.*
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

    @GetMapping("2/{grupo}/{sub}/{item}/texto")
    fun getTecidoAcabadoText(@PathVariable("grupo") grupo: String,
                             @PathVariable("sub") sub: String,
                             @PathVariable("item") item: String): String {
        val rep = TecidoAcabadoRepository(oracleTemplate)
        val data = rep.getTecidoData("2", grupo, sub, item)
        return rep.getText(data)
    }

    @GetMapping("4/{grupo}/{sub}/{item}/texto")
    fun getTecidoCruText(@PathVariable("grupo") grupo: String,
                         @PathVariable("sub") sub: String,
                         @PathVariable("item") item: String): String {
        val rep = TecidoCruRepository(oracleTemplate)
        val data = rep.getTecidoData("4", grupo, sub, item)
        return rep.getText(data)
    }

    @GetMapping("9/{grupo}/{sub}/{item}/texto")
    fun getInsumoText(@PathVariable("grupo") grupo: String,
                      @PathVariable("sub") sub: String,
                      @PathVariable("item") item: String): String {
        val rep = InsumoRepository(oracleTemplate)
        val data = rep.getInsumosData("9", grupo, sub, item)
        return rep.getText(data)
    }

    @GetMapping("{nivel}/{grupo}/{sub}/{item}/texto")
    fun getText(@PathVariable("nivel") nivel: String,
                @PathVariable("grupo") grupo: String,
                @PathVariable("sub") sub: String,
                @PathVariable("item") item: String): String {
        val rep = InsumoRepository(oracleTemplate)
        val data = rep.getInsumosData(nivel, grupo, sub, item)
        return rep.getText(data)
    }


}