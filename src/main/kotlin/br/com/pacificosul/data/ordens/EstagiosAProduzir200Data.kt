package br.com.pacificosul.data.ordens

data class EstagiosAProduzir200Data(val codigoEstagio: Int, val descricaoEstagio: String, val descricaoAgrupador: String,
                                    val apelido: String, val numeroRamal: Int, val quantidadePecasAProduzir: Number,
                                    val quantidadeOrdensAProduzir: Int) {
    fun getEstagioComDescricao() = codigoEstagio.toString().padStart(2,'0').plus("-").plus(descricaoEstagio)
    fun getResponsavelEstagio() = apelido.plus("(").plus(numeroRamal).plus(")")
}