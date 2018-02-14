package br.com.pacificosul.data.produto

import java.math.BigDecimal

open class ProdutoData {
    var descrReferencia: String? = null
    var descrTamRefer: String? = null
    var descricao15: String? = null
    var unidadeMedida: String? = null
    var artigoCota: String? = null
    var descrCtEstoque: String? = null
    var complemento: String? = null
    var rendimento: BigDecimal? = null
    var gramatura1: BigDecimal? = null
    var largura1: BigDecimal? = null
    var qtdeAreceber: Int? = null
    var qtdeReservadaGlobal: Int? = null
    val text: String
        get() = ""
}
