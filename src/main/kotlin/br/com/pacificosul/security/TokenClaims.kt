package br.com.pacificosul.security

data class TokenClaims(val apelido: String,
                       val cod_usuario: String,
                       val cracha: String,
                       val ip: String)