package br.com.pacificosul.data.suprimentos

import java.math.BigDecimal
import java.sql.Date

data class SUSData (val codigoSUS: Int?, val situacao: String?, val usuario: String?, val dataCorte: Date?,
                    val dataSolicitacao: Date?, val referencia: String?, val nivel: String?, val grupo: String?,
                    val subgrupo: String?, val item: String?, val descricao: String?, val complemento: String?,
                    val unidadeMedida: String?, val qtdeNecessaria: BigDecimal?, val ordemProducao: Int?,
                    val pedidoCompra: Int?, val dataEmissaoCorte: Date?, val fornecedor: String?,
                    val qtdePedidoAtual: BigDecimal?, val periodoProducao: Int?, val previsao1: Date?,
                    val previsao2: Date?, val previsao3: Date?, val fornecedorUltNF: String?,
                    val dataUltNF: Date?, val qtdeUltNF: BigDecimal?, val vlrUltNF: BigDecimal?)