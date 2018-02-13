package br.com.pacificosul.data

data class LocalizadorData(val nivel: String?, val grupo: String?, val subGrupo: String?, val item: String?,
                           val descricao: String?, val complemento: String?)

data class LocalizadorResultData(val nivel_estrutura: String?, val grupo_estrutura: String?,
                                 val subgru_estrutura: String?, val item_estrutura: String?,
                                 val descricao: String?, val complemento: String?,
                                 val qtde_areceber: Int?, val qtde_reservado: Int?,
                                 val qtde_estq_tmrp: Int?, val qtde_estq_global: Int?)