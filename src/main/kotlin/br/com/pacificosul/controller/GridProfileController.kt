package br.com.pacificosul.controller

import br.com.pacificosul.data.GridColumnsDefData
import br.com.pacificosul.data.GridProfileData
import br.com.pacificosul.repository.GridProfileRepository
import br.com.pacificosul.rules.getCodigoUsuario
import br.com.pacificosul.rules.getClaims
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

    @PostMapping("column/update")
    fun updateColumns(@RequestBody payload: List<GridColumnsDefData>) {
        payload.forEach { x -> GridProfileRepository(oracleTemplate).updateColumnsDef(x) }
    }

    @GetMapping("{gridName}/get-profiles")
    fun getProfiles(authentication: Authentication,
                    @PathVariable gridName: String): List<GridProfileData>{
        val codUsuario = getCodigoUsuario(authentication)
        return GridProfileRepository(oracleTemplate).getProfiles(gridName, codUsuario)
    }

    @PostMapping("{gridName}/create-profile")
    fun addProfile(authentication: Authentication,
                   @PathVariable gridName: String,
                   @RequestBody payload: Pair<String,  List<GridColumnsDefData>>): Pair<GridProfileData,  List<GridColumnsDefData>?>{
        val claims = getClaims(authentication)
        val gridId = GridProfileRepository(oracleTemplate).getGridId(gridName)
        val codUsuario = Integer.parseInt(claims.cod_usuario)
        val nomeUsuario = claims.apelido
        var profileName = payload.first
        if(profileName.isNullOrBlank()) profileName = "perfil-${nomeUsuario}"
        profileName = GridProfileRepository(oracleTemplate).getProfileName(gridName, profileName)
        val newProfileId = GridProfileRepository(oracleTemplate).createProfile(profileName, gridId, codUsuario)
        GridProfileRepository(oracleTemplate).createColumns(newProfileId, payload.second)
        GridProfileRepository(oracleTemplate).updateCurrentProfile(gridId, newProfileId, codUsuario)
        val newColumns = GridProfileRepository(oracleTemplate).getColumnsDef(newProfileId)
        val profileData = GridProfileData(newProfileId, profileName)
        return Pair(profileData, newColumns)
    }

    @PostMapping("{gridName}/profile/{id}/update-user-profile")
    fun updateUserProfile(authentication: Authentication,
                          @PathVariable gridName: String,
                          @PathVariable id: Int):  List<GridColumnsDefData>? {
        val gridId = GridProfileRepository(oracleTemplate).getGridId(gridName)
        val codUsuario = getCodigoUsuario(authentication)
        GridProfileRepository(oracleTemplate).updateCurrentProfile(gridId, id, codUsuario)
        return GridProfileRepository(oracleTemplate).getColumnsDef(id)
    }

    /* This is a temporary gambi to generate columns on db*/
    @PostMapping("{gridName}/temporary")
    fun processJson(@PathVariable gridName: String,
                    @RequestBody payload: List<GridColumnsDefData>) {
        val newGridId = GridProfileRepository(oracleTemplate).createGrid(gridName)
        val newProfileId = GridProfileRepository(oracleTemplate).createProfile("Padrão", newGridId, 3106)
        payload.forEach { data ->
            val newGridColumnId = GridProfileRepository(oracleTemplate).createGridColumn(newGridId, data)
            GridProfileRepository(oracleTemplate).createGridColumnPerfil(newProfileId, newGridColumnId, data)
        }
    }
}
