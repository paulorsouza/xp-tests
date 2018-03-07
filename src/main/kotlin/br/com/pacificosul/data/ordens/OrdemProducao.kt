package br.com.pacificosul.data.ordens

import java.math.BigDecimal
import java.sql.Date

data class OrdemProducaoData(val ordemProducao: Int, val OrdemProducaoPrincipal: Int? = null, val cancelamento: OrdemCancelamentoData? = null,
                             val periodoProducao: Int? = null, val referencia: String? = null)
data class OrdemCancelamentoPayload(val observacao: String)
data class OrdemCancelamentoData(val hasErrors: Boolean = false, val codigoCancelamento: Int = 0, val messages: Set<String> = emptySet())
data class OrdemProducaoItem(val ordemProducao: Int, val tamanho: String,
                             val cor: String, val quantidade: Int, val ordenacaoTamanho: Int)
data class OndeTemData(val periodoProducao: Int, val periodoAlterado: Int, val ordemProducao: Int,
                       val tipoOrdem: Int, val grupo: String, val item: String, val descricaColecao: String,
                       val codigoEstagio: Int, val descricaoEstagio: String, val leadTime: BigDecimal,
                       val qtdeOsManual: Int, val quantidadeFaltante: Int, val quantidadePrograma: Int,
                       val quantidadeProduzir: Int, val quantidadeProduzida: Int, val quantidadeSegunda: Int,
                       val quantidadePerda: Int, val quantidadeConserto: Int, val quantidadePendente: Int,
                       val tempoDesdeEstagio: Int, val codigoFamilia: Int, val desFamilia: String,
                       val ordemServico: Int?, val nomeTerceiro: String?, val artigoCotas: Int,
                       val descricaoArtigoCotas: String, val dataEmissao: Date, val dataPrevisao: Date,
                       val dataProrrogacao: Date, val quantidadePendencia: Int, val quantidadeUTI: Int)
data class OrdemFilhasData(val periodoProducao: Int, val ordemProducao: Int, val grupo: String, val item: String,
                       val descricaoEstagio: String, val quantidadeProgramado: Int, val quantidadeProduzir: Int,
                       val quantidadeProduzida: Int, val quantidadeSegunda: Int, val quantidadePerda: Int,
                       val quantidadeConserto: Int, val quantidadePendente: Int, val ordemServico: Int?,
                       val nomeTerceiro: String?)
data class OrdemAvance(val sequencia: Int, val codigoEstagio: Int, val descricaoEstagio: String, val qtdeProgramada: Int,
                       val qtdeProduzir: Int, val qtdeProduzido: Int, val qtdeSegunda: Int, val qtdePerda: Int,
                       val qtdeConserto: Int, val qtdePendente: Int, val diasEstagio: Int, val dataEntrada: Date?,
                       val dataBaixa: Date?, val horaEntrada: String?, val horaBaixa: String?, val usuario: String?)