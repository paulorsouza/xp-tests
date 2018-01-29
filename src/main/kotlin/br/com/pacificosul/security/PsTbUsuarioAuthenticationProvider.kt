package br.com.pacificosul.security

import br.com.pacificosul.databases.HikariCustomConfig
import org.omg.CORBA.Object
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component
import org.springframework.security.core.GrantedAuthority
import java.sql.Types
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Component
class PsTbUsuarioAuthenticationProvider : AuthenticationProvider {

    protected val jdbcTemplate: JdbcTemplate = JdbcTemplate(HikariCustomConfig().getOracleTemplate())

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {

        val credentials = authentication.principal as AccountCredentials
        val password = credentials.password
        val cracha = credentials.username
        val ip = credentials.ip

        println(credentials.ip)
        val sqlParameters = arrayListOf<String>(cracha, password)

        val sql = "SELECT DES_APELIDO, COD_USUARIO, COD_USUARIO_VETORH " +
                  "FROM PACIFICOSUL.PS_TB_USUARIO " +
                  "WHERE COD_USUARIO_VETORH = ? " +
                  "AND DES_SENHA = ? "

        val claims = jdbcTemplate.query(sql, sqlParameters.toArray()) {
            rs, _ -> TokenClaims(rs.getString(1), rs.getString(2), rs.getString(3), ip)
        }.orEmpty()

        if (claims.isNotEmpty()) {
            val tokenClaims = claims[0]
            updateIp(tokenClaims)
            return UsernamePasswordAuthenticationToken(
                    tokenClaims, password, ArrayList<GrantedAuthority>())
        } else {
            return null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    fun updateIp(tokenClaims: TokenClaims) {
        val update = "update pacificosul.ps_tb_usuario set ip_web = ? where cod_usuario = ?"
        val ip = tokenClaims.ip
        val codUsuario = tokenClaims.cod_usuario
        jdbcTemplate.update(update, ip, codUsuario)
    }
}