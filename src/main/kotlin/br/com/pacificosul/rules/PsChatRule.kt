package br.com.pacificosul.rules

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import br.com.pacificosul.data.PsChatData.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class PsChatRule {

    val BASE_URL = "http://localhost:3000"
    val AUTH_URL = "${BASE_URL}/api/v1/login"
    val CREATE_USER_URL = "${BASE_URL}/api/v1/users.create"

    fun auth(): UserAuth {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        // TODO create .properties to save this user
        val entity = HttpEntity<AuthPayload>(AuthPayload("teste", "1"), headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<UserAuth>(AUTH_URL,
                HttpMethod.POST, entity, UserAuth::class.java)
        return response.getBody()
    }

    fun hasChanges(ativo: Boolean, user: UserPayload, psUser: PsUser): Boolean {
        val fields = user.customFields
        val psUserAtivo = psUser.ativo == 1
        return ativo != psUserAtivo || psUser.nome != user.name || fields?.email != psUser.email ||
                psUser.senha != user.password || psUser.local != fields?.local || psUser.ramal != fields?.ramal
    }

    fun createUser(userAuth: UserAuth, user: UserPayload): String {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", userAuth.data.authToken)
        headers.set("X-User-Id", userAuth.data.userId)
        val entity = HttpEntity<UserPayload>(user, headers)
        val mapper = MappingJackson2HttpMessageConverter().objectMapper
        println(mapper.writeValueAsString(entity.body))
        println(entity.body)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<String>(CREATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        println(response.body)
        return response.body
    }
}