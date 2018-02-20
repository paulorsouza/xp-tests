package br.com.pacificosul.rules

import br.com.pacificosul.data.ordens.OrdemCancelamentoData
import br.com.pacificosul.repository.ordens.OrdemProducaoRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

fun cancelarOrdemProducao(jdbcTemplate: NamedParameterJdbcTemplate, ordemProducao: Int, observacao: String): OrdemCancelamentoData {
    val messages = HashSet<String>()
    val ordensConcat = getOrdensConcat(jdbcTemplate, ordemProducao)
    val hasRolos = OrdemProducaoRepository(jdbcTemplate).getHasRolosAlocados(ordensConcat)
    if (hasRolos) messages.add("Não é possível cancelar uma OP com rolos reservado/alocado.")
    val hasEstagios = OrdemProducaoRepository(jdbcTemplate).getHasProgramacaoAndSeparacao(ordensConcat)
    if (hasEstagios) messages.add("Não é possível cancelar uma OP com quantidade produzida")
    if (hasRolos || hasEstagios) {
        return OrdemCancelamentoData (
                hasErrors = true,
                codigoCancelamento = 1,
                messages = messages
        )
    }
    val rowUpdated = OrdemProducaoRepository(jdbcTemplate).cancelarOrdem(ordensConcat, 1, observacao)
    if (rowUpdated > 1)
        messages.add("As ordens ${ordensConcat.joinToString()} foram canceladas com sucesso.")
    else
        messages.add("A ordem $ordensConcat foi cancelada com sucesso.")
    return OrdemCancelamentoData (
            hasErrors = false,
            codigoCancelamento = 1,
            messages = messages
    )
}

fun getOrdensConcat(jdbcTemplate: NamedParameterJdbcTemplate, ordemProducao: Int): List<Int> {
    val ordensList = OrdemProducaoRepository(jdbcTemplate).getOrdensFilhas(ordemProducao)
    return ordensList.map{it.ordemProducao}
}