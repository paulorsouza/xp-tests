package br.com.pacificosul.security

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

    internal fun addAuthentication(response: HttpServletResponse, claims: TokenClaims) {
        val JWT = Jwts.builder()
            .claim("user", claims)
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact()
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT)
        response.writer.write(TOKEN_PREFIX + " " + JWT)
    }

    internal fun getAuthentication(request: HttpServletRequest): Authentication? {
        val token = request.getHeader(HEADER_STRING)

        if (token != null) {
            val user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .body["user"]

            if (user != null) {
                val data = user as Map<String, Any?>
                val tokenClaims = TokenClaims(
                        data.get("apelido").toString(),
                        data.get("cod_usuario").toString(),
                        data.get("cracha").toString(),
                        data.get("ip").toString())
                return UsernamePasswordAuthenticationToken(tokenClaims, null, emptyList<GrantedAuthority>())
            }
        }
        return null
    }

}