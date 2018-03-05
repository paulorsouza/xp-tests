package br.com.pacificosul.controller

import br.com.pacificosul.data.GridColumnsDefData
import br.com.pacificosul.repository.GridProfileRepository
import br.com.pacificosul.rules.getCodigoUsuario
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/grid")
class GridProfileController: DefaultController() {
    @GetMapping("{gridName}/current-profile")
    fun getCurrentProfile(authentication: Authentication,
                          @PathVariable("gridName") gridName: String): Int? {
        val codUsuario = getCodigoUsuario(authentication)
        return GridProfileRepository(oracleTemplate).getCurrentProfile(gridName, codUsuario)
    }

    @GetMapping("{gridName}/profile/{id}/columns")
    fun getColumnsDef(@PathVariable("id") id: Int): List<GridColumnsDefData>? {
        return GridProfileRepository(oracleTemplate).getColumnsDef(id)
    }

    @GetMapping("{gridName}/current-columns")
    fun getColumnsDef(authentication: Authentication,
                      @PathVariable("gridName") gridName: String): List<GridColumnsDefData>? {
        val codUsuario = getCodigoUsuario(authentication)
        val profileId = GridProfileRepository(oracleTemplate).getCurrentProfile(gridName, codUsuario)
        return GridProfileRepository(oracleTemplate).getColumnsDef(profileId!!)
    }
}