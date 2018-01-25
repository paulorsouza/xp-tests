package br.com.pacificosul.model

data class Referencia(val nivel: String, val grupo: String, val subgrupo: String, val item: String) {
    fun getConcat(): String = nivel.plus(".").plus(grupo).plus(".").plus(subgrupo).plus(".").plus(item)
}
data class Produto(val referencia: Referencia)