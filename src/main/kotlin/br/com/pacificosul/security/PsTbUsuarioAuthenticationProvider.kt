package br.com.pacificosul.security

import br.com.pacificosul.databases.HikariCustomConfig
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.ArrayList
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component
import org.springframework.security.core.GrantedAuthority

@Component
class PsTbUsuarioAuthenticationProvider : AuthenticationProvider {

    protected val jdbcTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getOracleTemplate())

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {

        val name = authentication.getName()
        val password = authentication.getCredentials().toString()
        val sqlParameters = arrayListOf<String>(name, password)

        val sql = "SELECT DES_APELIDO, COD_USUARIO, COD_USUARIO_VETORH " +
                  "FROM PACIFICOSUL.PS_TB_USUARIO " +
                  "WHERE COD_USUARIO_VETORH = ? " +
                  "AND DES_SENHA = ? "

        val claims = jdbcTemplate.query(sql, sqlParameters.toArray()) {
            rs, _ -> TokenClaims(rs.getString(1), rs.getString(2), rs.getString(3))
        }.orEmpty()

        if (claims.isNotEmpty()) {
            return UsernamePasswordAuthenticationToken(
                    claims[0], password, ArrayList<GrantedAuthority>())
        } else {
            return null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}