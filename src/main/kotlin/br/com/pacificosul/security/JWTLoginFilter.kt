package br.com.pacificosul.security

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.core.GrantedAuthority
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.core.JsonParser.Feature
import java.util.stream.Collectors

class JWTLoginFilter (url: String, authManager: AuthenticationManager) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(url)) {

    init {
        authenticationManager = authManager
    }

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val mapper = jacksonObjectMapper()
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true)
        mapper.configure(Feature.AUTO_CLOSE_SOURCE, true)
        val credentials = mapper.readValue(request.inputStream, AccountCredentials::class.java)

        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.username,
                credentials.password,
                emptyList<GrantedAuthority>()
            )
        )
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain?,
            auth: Authentication) {
        TokenAuthenticationService.addAuthentication(response, auth.name)
    }

}