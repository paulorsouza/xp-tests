package br.com.pacificosul.data

import java.sql.Date

data class TotalPedidosRecebidosData(val numeroPedido: Int, val estado: String, val situacao: String,
                            val empresa: String, val regiao: String, val codigoRepresentante: Int, val apelido: String,
                            val quantidadePedido: Double, val totalPedido: Double, val codigoPeriodo: Int,
                            val descricaoPeriodo: String)
data class PedidosRecebidosData(val apelido: String, val estado: String?, val codRepresentante: Int?,
                                val valorPedido: Double?, val dataRecebido: Date?, val situacao: String?,
                                val numeroPedido: Int?, val dataEntrega1: Date?, val dataEntrega2: Date?,
                                val numeroDocumento: Long?, val nomeCliente: String?, val cidade: String?,
                                val pedidoVinculado: Int?, val quantidadePedido: Int?, val periodo: String?,
                                val pertenceRegiao: Boolean?, val representanteRegiao: String?, val marcas: String?,
                                val pedidoIntegrado: Boolean?)