package br.com.pacificosul.data.produto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
open class ProdutoData {
    var nivel: String? = null
    var grupo: String? = null
    var subGrupo: String? = null
    var item: String? = null
    var descricao: String? = null
    var descrReferencia: String? = null
    var descrTamRefer: String? = null
    var descricao15: String? = null
    var unidadeMedida: String? = null
    var artigoCota: String? = null
    var descrCtEstoque: String? = null
    var complemento: String? = null
    val text: String
        get() = ""
}
