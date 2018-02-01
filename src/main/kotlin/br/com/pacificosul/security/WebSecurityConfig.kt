package br.com.pacificosul.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.context.annotation.Bean

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var authenticationProvider: PsTbUsuarioAuthenticationProvider

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.cors().and().csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.GET, "/api/images/download").permitAll()
            .antMatchers(HttpMethod.GET, "/api/user/*/apelido").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(JWTLoginFilter("/login", authenticationManager()),
                UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JWTAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java)
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(authenticationProvider)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = arrayListOf("http://localhost:3000")
        configuration.allowedMethods = arrayListOf("POST", "PUT", "OPTIONS", "GET", "DELETE")
        configuration.maxAge = 3600
        configuration.allowedHeaders = arrayListOf("Authorization", "Origin", "Content-Type")
        configuration.exposedHeaders = arrayListOf("Authorization", "Origin", "Content-Type")
        configuration.allowCredentials = false
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}