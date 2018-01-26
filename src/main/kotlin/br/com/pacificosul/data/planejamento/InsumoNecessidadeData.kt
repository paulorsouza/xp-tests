package br.com.pacificosul.data.planejamento

import br.com.pacificosul.model.Referencia
import java.math.BigDecimal

data class InsumoNecessidadeData(val referencia: Referencia, val estoqueAtual: BigDecimal,
                                 val quantidadeAReceber: BigDecimal, val quantidadeReservaGlobal: BigDecimal,
                                 val descricao: String, val quantidadeReservada: BigDecimal,
                                 val consumoMedio: BigDecimal) {
    fun getNivel() = referencia.nivel
    fun getGrupo() = referencia.grupo
    fun getSubgrupo() = referencia.subgrupo
    fun getItem() = referencia.item
}