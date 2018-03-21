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
    val UPDATE_USER_URL = "${BASE_URL}/api/v1/users.update"

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

    fun getUserId(auth: UserAuth, userName: String): String? {
        val userUrl = getUserUrl(userName)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", auth.data.authToken)
        headers.set("X-User-Id", auth.data.userId)
        val entity = HttpEntity<UserPayload>(headers)
        val restTemplate = RestTemplate()
        try {
            val response = restTemplate.exchange<UserInfoResponse>(userUrl,
                    HttpMethod.GET, entity, UserInfoResponse::class.java)
            val userInfo = response.body
            return userInfo.user._id
        } catch(ex: Exception) {
            return null
        }
    }

    fun createUser(auth: UserAuth, user: UserPayload): String {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", auth.data.authToken)
        headers.set("X-User-Id", auth.data.userId)
        val entity = HttpEntity<UserPayload>(user, headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<String>(CREATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        return response.body
    }

    fun updateUser(auth: UserAuth, user: UpdateUserPayload) {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Auth-Token", auth.data.authToken)
        headers.set("X-User-Id", auth.data.userId)
        val entity = HttpEntity<UpdateUserPayload>(user, headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<String>(UPDATE_USER_URL,
                HttpMethod.POST, entity, String::class.java)
        println(response.body)
    }
}