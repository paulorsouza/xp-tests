package br.com.pacificosul.security

import java.util.Collections
import java.util.Date

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.GrantedAuthority

object TokenAuthenticationService {

    internal val EXPIRATION_TIME: Long = 860000000
    internal val SECRET = "MySecret"
    internal val TOKEN_PREFIX = "Bearer"
    internal val HEADER_STRING = "Authorization"

    internal fun addAuthentication(response: HttpServletResponse, username: String) {
        val JWT = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact()
        response.writer.write(TOKEN_PREFIX + " " + JWT)
    }

    internal fun getAuthentication(request: HttpServletRequest): Authentication? {
        val token = request.getHeader(HEADER_STRING)

        if (token != null) {
            val user = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .body
                .subject

            println(user)

            if (user != null) {
                return UsernamePasswordAuthenticationToken(user, null, emptyList<GrantedAuthority>())
            }
        }
        return null
    }

}