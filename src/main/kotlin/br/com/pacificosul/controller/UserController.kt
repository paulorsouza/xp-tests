package br.com.pacificosul.controller

import br.com.pacificosul.repository.UserRepository
import br.com.pacificosul.security.TokenClaims
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import br.com.pacificosul.exceptions.ForbiddenException

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/user")
class UserController : DefaultController() {

    @GetMapping("/currentUser")
    fun getCurrentUser(authentication: Authentication): TokenClaims {
        val claims = authentication.principal as TokenClaims
        val sameIp = UserRepository(oracleTemplate).sameIp(Integer.parseInt(claims.cod_usuario), claims.ip)
        if(!sameIp) throw ForbiddenException()
        return claims
    }

    @GetMapping("/{codUser}/apelido")
    fun getApelido(@PathVariable("codUser") codUser: Int): String? {
        return UserRepository(oracleTemplate).getApelido(codUser)
    }
}