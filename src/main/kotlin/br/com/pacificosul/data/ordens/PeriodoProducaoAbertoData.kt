package br.com.pacificosul.data.ordens

data class PeriodoProducaoAbertoData(val codigoPeriodo: Int, val descricaoPeriodo: String?, val quantidadePecas: Number,
                                     val quantidadeOrdens: Int) {
    fun getPeriodoComDescricao() = codigoPeriodo.toString().plus(" - ").plus(descricaoPeriodo)
}