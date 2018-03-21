package br.com.pacificosul.rules

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import br.com.pacificosul.data.PsChatData.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class PsChatRule {

    val BASE_URL = "http://187.84.228.4:3000"
    val AUTH_URL = "${BASE_URL}/api/v1/login"
    val CREATE_USER_URL = "${BASE_URL}/api/v1/users.create"

    fun auth(userName: String, password: String): UserAuth {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<AuthPayload>(AuthPayload(userName, password), headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<UserAuth>(AUTH_URL,
                HttpMethod.POST, entity, UserAuth::class.java)
        return response.getBody()
    }

    fun createUser(userAuth: UserAuth, user: UserPayload): String {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", userAuth.data.authToken)
        headers.set("X-User-Id", userAuth.data.userId)
        val entity = HttpEntity<UserPayload>(user, headers)
        val mapper = MappingJackson2HttpMessageConverter().objectMapper
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<String>(CREATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        return response.body
    }
}