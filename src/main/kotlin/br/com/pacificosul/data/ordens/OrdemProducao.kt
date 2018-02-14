package br.com.pacificosul.data.ordens

import java.math.BigDecimal
import java.sql.Date

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