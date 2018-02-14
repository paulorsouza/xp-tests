package br.com.pacificosul.rules

import br.com.pacificosul.security.TokenClaims
import org.springframework.security.core.Authentication

fun getCodigoUsuario(authentication: Authentication): Int {
    val claims = authentication.principal as TokenClaims
    return Integer.parseInt(claims.cod_usuario)
}

fun getCodigoCracha(authentication: Authentication): Int {
    val claims = authentication.principal as TokenClaims
    return Integer.parseInt(claims.cracha)
}

fun getClaims(authentication: Authentication): TokenClaims {
    return authentication.principal as TokenClaims
}