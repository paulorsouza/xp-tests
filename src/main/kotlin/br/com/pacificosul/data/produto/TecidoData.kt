package br.com.pacificosul.data.produto

import java.math.BigDecimal

open class TecidoData: ProdutoData() {
    var rendimento: BigDecimal? = null
    var gramatura1: BigDecimal? = null
    var largura1: BigDecimal? = null
    var qtdeAreceber: Int? = null
    var qtdeReservadaGlobal: Int? = null
    var valorCusto: BigDecimal? = null
}