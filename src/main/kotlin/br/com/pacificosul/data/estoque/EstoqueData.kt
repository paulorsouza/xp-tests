package br.com.pacificosul.data.estoque

import java.math.BigDecimal

data class EstoqueDeposito(val codigoDeposito: Int, val descricaoDeposito: String, val quantidade: BigDecimal) {
    fun getConcat() = this.codigoDeposito.toString().plus(" - ").plus(this.descricaoDeposito)
}