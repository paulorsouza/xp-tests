package br.com.pacificosul.data

class PsChatData {
    data class AuthPayload(val username: String, val password: String)
    data class UserAuth(val status: String, val data: DataAuth)
    data class DataAuth(val userId: String, val authToken: String)
    data class UserPayload(val name: String, val email: String, val password: String, val username: String,
                           val customFields: CustomFieldsData)
    data class UserResponse(val _id: String, val rule: List<String>)
    data class CustomFieldsData(val ramal: Int, val email: String, val local: String)
    data class PsUser(val cracha: String, val nome: String, val senha: String, val local: String, val ramal: Int,
                      val ativo: Int, val email: String)
}
