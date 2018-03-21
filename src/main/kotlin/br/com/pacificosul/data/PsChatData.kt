package br.com.pacificosul.data

class PsChatData {
    data class AuthPayload(val username: String, val password: String)
    data class UserAuth(val status: String, val data: DataAuth)
    data class DataAuth(val userId: String, val authToken: String)
    data class UserResponse(val _id: String, val rule: List<String>)
    data class UserPayload(val name: String, val email: String, val password: String, val username: String,
                           val customFields: CustomFieldsData)
    data class CustomFieldsData(val ramal: String?, val email: String?, val local: String?)
    data class PsUser(val cracha: String, val nome: String, val senha: String, val local: String, val ramal: Int,
                      val ativo: Int, val email: String)
    data class UserInfoData(val _id: String)
    data class UserInfoResponse(val user: UserInfoData)
    data class UpdateUserPayload(val userId: String, val data: UpdateUserData)
    data class UpdateUserData(val email: String?, val password: String?, val username: String?,
                              val active: Boolean?, val customFields: CustomFieldsData?)
}
