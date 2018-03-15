package br.com.pacificosul.rules

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

class PsChatRule {

    val BASE_URL = "http://localhost:3000"
    val AUTH_URL = "${BASE_URL}/api/v1/login"
    val CREATE_USER_URL = "${BASE_URL}/api/v1/users.create"

    fun auth(): UserAuth {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        // TODO create .properties to save this user
        val entity = HttpEntity<AuthPayload>(AuthPayload("paulo", "1"), headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<UserAuth>(AUTH_URL,
                HttpMethod.POST, entity, UserAuth::class.java)
        return response.getBody()
    }

    fun createUser(userAuth: UserAuth, user: UserPayload): UserPayload {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", userAuth.data.authToken)
        headers.set("X-User-Id", userAuth.data.userId)
        val entity = HttpEntity<UserPayload>(user, headers)
        val restTemplate = RestTemplate()
        restTemplate.exchange<String>(CREATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        return user
    }

    data class AuthPayload(val username: String, val password: String) {}
    data class UserAuth(val status: String, val data: DataAuth) {}
    data class DataAuth(val userId: String, val authToken: String) {}
    data class UserPayload(val name: String, val email: String, val password: String, val userName: String,
                           val customFields: CustomFieldsData)
    data class CustomFieldsData(val ramal: Int, val cargo: String, val email: String)
}