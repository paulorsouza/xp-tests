package br.com.pacificosul.controller.suprimentos

import br.com.pacificosul.controller.DefaultController
import br.com.pacificosul.data.suprimentos.SUSData
import br.com.pacificosul.repository.suprimentos.SUSRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/suprimento/")
class SuprimentoController : DefaultController() {
    @GetMapping("/sus")
    fun solicitacaoUrgente(@RequestParam(name= "nivel", required = false)nivel: String?,
                           @RequestParam(name= "grupo", required = false)grupo: String?,
                           @RequestParam(name= "subgrupo", required = false)subgrupo: String?,
                           @RequestParam(name= "item", required = false)item: String?,
                           @RequestParam(name= "nomeFornecedor", required = false)nomeFornecedor: String?,
                           @RequestParam(name= "ordemProducao", required = false)ordemProducao: Int?,
                           @RequestParam(name= "situacoes", required = false)situacoes: List<Int>?): List<SUSData> {
        System.out.println(nivel);
        System.out.println(grupo);
        System.out.println(subgrupo);
        System.out.println(item);
        System.out.println(nomeFornecedor);
        System.out.println(ordemProducao);
        System.out.println(situacoes);
        return SUSRepository(oracleTemplate).solicitacaoUrgente(
                nivel.orEmpty().toUpperCase(),
                grupo.orEmpty().toUpperCase(),
                subgrupo.orEmpty().toUpperCase(),
                item.orEmpty().toUpperCase(),
                ordemProducao,
                nomeFornecedor.orEmpty().toUpperCase(),
                situacoes)
    }
}