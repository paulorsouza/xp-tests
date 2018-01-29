package br.com.pacificosul.security

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountCredentials(@JsonProperty("username") val username: String,
                              @JsonProperty("password") val password: String,
                              @JsonProperty("ip") val ip: String)