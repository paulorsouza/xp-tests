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

        val sql = "SELECT DES_APELIDO FROM PACIFICOSUL.PS_TB_USUARIO " +
                  "WHERE COD_USUARIO_VETORH = ? " +
                  "AND DES_SENHA = ? "

        val apelido = jdbcTemplate.query(sql, sqlParameters.toArray()) {
            rs, _ -> rs.getString(1)
        }.orEmpty()

        println(apelido)

        if (apelido.isNotEmpty()) {
            return UsernamePasswordAuthenticationToken(
                    apelido[0], password, ArrayList<GrantedAuthority>())
        } else {
            return null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}