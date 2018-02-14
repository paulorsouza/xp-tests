package br.com.pacificosul.data.produto

import br.com.pacificosul.repository.produto.PecaRepository

class PecaData: ProdutoData() {
    var referencia: String? = null
    var descrColecao: String? = null
    var artigoProduto: String? = null
    var descrSerie: String? = null
}