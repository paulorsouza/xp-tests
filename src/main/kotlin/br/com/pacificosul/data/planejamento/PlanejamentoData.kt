package br.com.pacificosul.data.planejamento

import java.math.BigDecimal
import java.sql.Date

data class RolosOrdemData(val periodoProducao: Int, val codigoRolo: Int, val codigoDeposito: Int,
                          val numeroLote: String?, val pesoRolo: BigDecimal, val ordemProducao: Int,
                          val tipOrdem: String?, val descricaoColecao: String?, val dataReserva: Date?,
                          val estagioOP: String?
                          ) {
    fun getDesOp() = ordemProducao.toString().plus(" - ").plus(descricaoColecao).plus(" ").plus(tipOrdem)
}