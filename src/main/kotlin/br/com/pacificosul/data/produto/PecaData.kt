package br.com.pacificosul.data.produto

class PecaData {
    var referencia: String? = null
    var descrColecao: String? = null
    var artigoProduto: String? = null
    var descrSerie: String? = null
    val text: String
        get() {
            val builder = StringBuilder()
            builder.append("Coleção Atual: ${descrColecao.orEmpty()}")
            builder.append("   Endereço: ")
            return builder.toString()
        }
}