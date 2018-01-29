package br.com.pacificosul.controller

import br.com.pacificosul.data.TotalPedidosRecebidosData
import br.com.pacificosul.rules.totalPedidosRecebidos
import br.com.pacificosul.security.TokenClaims
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/user")
class UserController : DefaultController() {

    @GetMapping("/currentUser")
    fun getCurrentUser(authentication: Authentication): TokenClaims {
        val claims = authentication.principal as TokenClaims
        return claims
    }
}