package br.com.pacificosul.rules

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import br.com.pacificosul.data.PsChatData.*

class PsChatRule {

    val BASE_URL = "http://187.84.228.4:3000"
    val AUTH_URL = "${BASE_URL}/api/v1/login"
    val CREATE_USER_URL = "${BASE_URL}/api/v1/users.create"
    val USER_INFO_URL = "${BASE_URL}/api/v1/users.info"

    fun getUserUrl(userName: String): String {
        return "${USER_INFO_URL}?username=${userName}"
    }

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

    fun userExists(userAuth: UserAuth, userName: String): Boolean {
        val userUrl = getUserUrl(userName)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", userAuth.data.authToken)
        headers.set("X-User-Id", userAuth.data.userId)
        val entity = HttpEntity<UserPayload>(headers)
        val restTemplate = RestTemplate()
        try {
            val response = restTemplate.exchange<String>(userUrl,
                    HttpMethod.GET, entity, String::class.java)
            return true
        } catch(ex: Exception) {
            return false
        }
    }

    fun createUser(userAuth: UserAuth, user: UserPayload): String {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", userAuth.data.authToken)
        headers.set("X-User-Id", userAuth.data.userId)
        val entity = HttpEntity<UserPayload>(user, headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<String>(CREATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        return response.body
    }
}