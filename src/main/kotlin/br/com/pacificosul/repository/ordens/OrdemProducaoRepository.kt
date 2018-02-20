package br.com.pacificosul.repository.ordens

import br.com.pacificosul.data.ordens.OndeTemData
import br.com.pacificosul.data.ordens.OrdemFilhasData
import br.com.pacificosul.data.ordens.OrdemProducaoData
import br.com.pacificosul.data.ordens.OrdemProducaoItem
import br.com.pacificosul.data.produto.LocalizadorResultData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class OrdemProducaoRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun getOrdemItens(ordemProducao: Int): List<OrdemProducaoItem> {
        val sql = "select pcpc_021.ordem_producao, pcpc_021.tamanho, pcpc_021.sortimento, " +
                "pcpc_021.sequencia_tamanho, pcpc_021.quantidade from pcpc_021 " +
                "where pcpc_021.ordem_producao = :ordemProducao"
        val mapa = HashMap<String, Any>()
        mapa["ordemProducao"] = ordemProducao

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> OrdemProducaoItem(rs.getInt("ordem_producao"),
                rs.getString("tamanho"), rs.getString("sortimento"),
                rs.getInt("quantidade"), rs.getInt("sequencia_tamanho"))
        }
    }

    fun getOndeTem(grupo: String): List<OndeTemData> {
        val sql = "select " +
                "ordpend.periodo_producao as periodo_producao, ordpend.periodo_alterado as periodo_alterado, " +
                "ordpend.ordem_producao as ordem_producao ,ordpend.tipo_ordem, " +
                "ordpend.referencia as grupo,ordpend.item,ordpend.descr_colecao,ordpend.codigo_estagio,ordpend.descricao_estagio,ordpend.lead_time, " +
                "(   select " +
                "    count(*) " +
                "    from PS_TB_OSMANUAL " +
                "    where PS_TB_OSMANUAL.ORDEM_PRODUCAO = ordpend.ordem_producao " +
                ") as qtde_osmanual, " +
                "PS_FN_CALC_DISP_LIQUIDA(1,ordpend.referencia, ordpend.item, 0,9999 ) as qtde_falta, " +
                "sum(qtde_programada) as qtde_programada, " +
                "sum(qtde_produzir) as qtde_produzir, " +
                "sum(qtde_produzida) as qtde_produzida, " +
                "sum(qtde_segunda) as qtde_segunda, " +
                "sum(qtde_perda) as qtde_perda, " +
                "sum(qtde_conserto) as qtde_conserto, " +
                "sum(qtde_pendente) qtde_pendente, " +
                "nvl(PS_GET_TEMPO_DESDE_ESTAGIO(ordem_producao, codigo_estagio),0) as tempo_desde_estagio, " +
                "ordpend.cod_familia, " +
                "ordpend.des_familia,ordpend.ordem_servico,ordpend.nome_terceiro, ordpend.artigo_cotas, ordpend.descr_artigo, " +
                "ordpend.data_emissao,ordpend.data_previsao,ordpend.data_prorrogacao,ordpend.qtde_pendencia,ordpend.qtde_uti " +
                "from " +
                "( " +
                "    select /*+ index */ " +
                "    pcpc_020.PERIODO_PRODUCAO, " +
                "    decode(pcpc_040.PERIODO_PRODUCAO,pcpc_020.PERIODO_PRODUCAO,0,1) as periodo_alterado, " +
                "    pcpc_040.ORDEM_PRODUCAO, " +
                "    pcpc_020.ROTEIRO_PECA, " +
                "    pcpc_020.ALTERNATIVA_PECA, " +
                "    pcpc_020.TIPO_ORDEM, " +
                "    pcpc_020.ORDEM_PRINCIPAL, " +
                "    pcpc_040.ORDEM_CONFECCAO, " +
                "    pcpc_020.DATA_PROGRAMACAO, " +
                "    pcpc_020.REFERENCIA_PECA as referencia, " +
                "    pcpc_040.PROCONF_ITEM as item, " +
                "    basi_140.DESCR_COLECAO, " +
                "    pcpc_040.CODIGO_ESTAGIO, " +
                "    mqop_005.DESCRICAO as descricao_estagio, " +
                "    mqop_005.LEED_TIME as lead_time, " +
                "    pcpc_040.SEQUENCIA_ESTAGIO, " +
                "    pcpc_020.ULTIMO_ESTAGIO, " +
                "    pcpc_040.QTDE_PECAS_PROG as qtde_programada, " +
                "    nvl(pcpc_040.QTDE_A_PRODUZIR_PACOTE,0) qtde_produzir, " +
                "    pcpc_040.QTDE_PECAS_PROD as qtde_produzida, " +
                "    pcpc_040.QTDE_PECAS_2A as qtde_segunda, " +
                "    pcpc_040.QTDE_CONSERTO as qtde_conserto, " +
                "    pcpc_040.QTDE_PERDAS as qtde_perda, pcpc_040.qtde_em_producao_pacote as qtde_pendente, " +
                "    (   select " +
                "        count(*) " +
                "        from PSPENDENCIA_OP " +
                "        where (PSPENDENCIA_OP.SIT_PENDENCIA = 'A') " +
                "        and PSPENDENCIA_OP.COD_ORDEMPRODUCAO = pcpc_040.ORDEM_PRODUCAO " +
                "    ) as qtde_pendencia, " +
                "    (   select " +
                "        count(*) " +
                "        from PS_TB_SOLICITACAO_SUPRIMENTO uti " +
                "        where uti.num_op = pcpc_040.ordem_producao " +
                "        and uti.COD_NIVEL = pcpc_040.PROCONF_NIVEL99 " +
                "        and uti.COD_GRUPO = pcpc_040.PROCONF_GRUPO " +
                "        and uti.COD_SUBGRUPO = pcpc_040.PROCONF_SUBGRUPO " +
                "        and uti.COD_ITEM = pcpc_040.PROCONF_ITEM " +
                "        and uti.SIT_SOLICITACAO < 2 " +
                "    ) as qtde_uti, " +
                "    pcpc_040.CODIGO_FAMILIA as cod_familia, " +
                "    basi_180.DESCRICAO as des_familia, " +
                "    obrf_081.NUMERO_ORDEM as ordem_servico, " +
                "    nvl(obrf_080.DATA_EMISSAO, to_date('19800101','yyyymmdd')) as data_emissao, " +
                "    nvl(obrf_081.DT_PREV_ARECEBER, to_date('19800101','yyyymmdd')) as data_previsao, " +
                "    (   select nvl(max(PSPRORROGACAO_ORDEM_SERVICO.DAT_PRORROGACAO), to_date('19800101','yyyymmdd')) " +
                "        from PSPRORROGACAO_ORDEM_SERVICO " +
                "        where PSPRORROGACAO_ORDEM_SERVICO.NUM_ORDEM = obrf_081.NUMERO_ORDEM " +
                "    ) as data_prorrogacao, " +
                "    supr_010.NOME_FORNECEDOR as nome_terceiro, basi_030.artigo_cotas, basi_295.descr_artigo " +
                "    from pcpc_040 " +
                "    left join mqop_005 on (pcpc_040.CODIGO_ESTAGIO = mqop_005.CODIGO_ESTAGIO) " +
                "    join basi_030 on (basi_030.NIVEL_ESTRUTURA = pcpc_040.PROCONF_NIVEL99 " +
                "          and basi_030.REFERENCIA = pcpc_040.PROCONF_GRUPO) " +
                "    join basi_140 on (basi_140.COLECAO = basi_030.COLECAO) " +
                "    left join obrf_081 on (obrf_081.NUMERO_ORDEM = PACIFICOSUL.PS_GET_ORDEM_SERVICO_OP(pcpc_040.ORDEM_PRODUCAO, pcpc_040.ORDEM_CONFECCAO, pcpc_040.CODIGO_ESTAGIO) " +
                "                    and obrf_081.PRODORD_NIVEL99 = pcpc_040.PROCONF_NIVEL99 " +
                "                    and obrf_081.PRODORD_GRUPO = pcpc_040.PROCONF_GRUPO " +
                "                    and obrf_081.PRODORD_SUBGRUPO = pcpc_040.PROCONF_SUBGRUPO " +
                "                    and obrf_081.PRODORD_ITEM = pcpc_040.PROCONF_ITEM) " +
                "    left join obrf_080 on (obrf_080.NUMERO_ORDEM = obrf_081.NUMERO_ORDEM) " +
                "    left join supr_010 on (supr_010.FORNECEDOR9 = obrf_080.CGCTERC_FORNE9 " +
                "                    and supr_010.FORNECEDOR4 = obrf_080.CGCTERC_FORNE4 " +
                "                    and supr_010.FORNECEDOR2 = obrf_080.CGCTERC_FORNE2) " +
                "    left join basi_180 on (pcpc_040.CODIGO_FAMILIA = basi_180.DIVISAO_PRODUCAO) " +
                "    join pcpc_020 on (pcpc_040.ORDEM_PRODUCAO = pcpc_020.ORDEM_PRODUCAO) left join basi_295 on (basi_030.artigo_cotas = basi_295.artigo_cotas) " +
                "    where pcpc_020.COD_CANCELAMENTO = 0 " +
                "          and pcpc_040.PROCONF_GRUPO = :grupo " +
                "          and (pcpc_020.PERIODO_PRODUCAO > 1700 or pcpc_020.TIPO_ORDEM = 2 or pcpc_020.TIPO_ORDEM = 3)" +
                ")ordpend "+
                "having sum(qtde_pendente) > 0 " +
                "group by ordpend.ordem_producao, " +
                "ordpend.tipo_ordem,ordpend.periodo_producao,ordpend.codigo_estagio,ordpend.descr_colecao, " +
                "ordpend.nome_terceiro, ordpend.artigo_cotas, ordpend.descr_artigo,ordpend.descricao_estagio, " +
                "ordpend.lead_time,ordpend.referencia,ordpend.cod_familia,ordpend.des_familia,ordpend.ordem_servico, " +
                "ordpend.data_emissao,ordpend.data_previsao,ordpend.data_prorrogacao,ordpend.qtde_pendencia, " +
                "ordpend.data_prorrogacao,ordpend.nome_terceiro,ordpend.item,ordpend.periodo_alterado,ordpend.ultimo_estagio, " +
                "ordpend.ROTEIRO_PECA,ordpend.ALTERNATIVA_PECA,ordpend.qtde_uti " +
                "order by ordpend.tipo_ordem desc, ordpend.periodo_producao, qtde_falta "

        val mapa = HashMap<String, Any>()
        mapa["grupo"] = grupo

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> OndeTemData(rs.getInt("periodo_producao"), rs.getInt("periodo_alterado"),
                rs.getInt("ordem_producao"), rs.getInt("tipo_ordem"), rs.getString("grupo"), rs.getString("item"),
                rs.getString("descr_colecao"), rs.getInt("codigo_estagio"), rs.getString("descricao_estagio"),
                rs.getBigDecimal("lead_time"), rs.getInt("qtde_osmanual"), rs.getInt("qtde_falta"),
                rs.getInt("qtde_programada"), rs.getInt("qtde_produzir"), rs.getInt("qtde_produzida"),
                rs.getInt("qtde_segunda"), rs.getInt("qtde_perda"), rs.getInt("qtde_conserto"), rs.getInt("qtde_pendente"),
                rs.getInt("tempo_desde_estagio"), rs.getInt("cod_familia"), rs.getString("des_familia"),
                rs.getInt("ordem_servico"), rs.getString("nome_terceiro"), rs.getInt("artigo_cotas"),
                rs.getString("descr_artigo"), rs.getDate("data_emissao"), rs.getDate("data_previsao"),
                rs.getDate("data_prorrogacao"), rs.getInt("qtde_pendencia"), rs.getInt("qtde_uti"))
        }.orEmpty()
    }

    fun getOrdemFilhas(ordemProducao: Int): List<OrdemFilhasData> {
        val sql = "select " +
                "ordpend.periodo_producao, ordpend.periodo_alterado, ordpend.ordem_producao, " +
                "ordpend.tipo_ordem, ordpend.referencia as grupo, ordpend.item, " +
                "ordpend.descr_colecao, ordpend.codigo_estagio, ordpend.descricao_estagio, " +
                "ordpend.lead_time, " +
                "( " +
                "  select " +
                "  count(*) " +
                "  from PS_TB_OSMANUAL " +
                "  where PS_TB_OSMANUAL.ORDEM_PRODUCAO = ordpend.ordem_producao " +
                ") as qtde_osmanual, " +
                "sum(qtde_programada) as qtde_programada, " +
                "sum(qtde_produzir) as qtde_produzir, " +
                "sum(qtde_produzida) as qtde_produzida, " +
                "sum(qtde_segunda) as qtde_segunda, " +
                "sum(qtde_perda) as qtde_perda, " +
                "sum(qtde_conserto) as qtde_conserto, " +
                "(sum(qtde_produzir) - sum(qtde_produzida) - sum(qtde_segunda) -  + sum(qtde_perda)) as qtde_pendente, " +
                "nvl(PS_GET_TEMPO_DESDE_ESTAGIO(ordem_producao, codigo_estagio),0) as tempo_desde_estagio, " +
                "ordpend.cod_familia, " +
                "ordpend.des_familia, " +
                "ordpend.ordem_servico, " +
                "ordpend.nome_terceiro, " +
                "ordpend.data_emissao, " +
                "ordpend.data_previsao, " +
                "ordpend.data_prorrogacao, " +
                "ordpend.qtde_pendencia, " +
                "( " +
                "  select " +
                "  count(*) " +
                "  from PS_TB_SOLICITACAO_SUPRIMENTO uti " +
                "  where uti.num_op = ordpend.ordem_producao  " +
                "  and uti.SIT_SOLICITACAO < 2 " +
                ") as qtde_uti " +
                "from " +
                "( " +
                "    select " +
                "    pcpc_020.PERIODO_PRODUCAO, " +
                "    decode(pcpc_040.PERIODO_PRODUCAO,pcpc_020.PERIODO_PRODUCAO,0,1) as periodo_alterado, " +
                "    pcpc_040.ORDEM_PRODUCAO, " +
                "    pcpc_020.ROTEIRO_PECA, " +
                "    pcpc_020.ALTERNATIVA_PECA, " +
                "    pcpc_020.TIPO_ORDEM, " +
                "    pcpc_020.ORDEM_PRINCIPAL, " +
                "    pcpc_040.ORDEM_CONFECCAO, " +
                "    pcpc_020.DATA_PROGRAMACAO, " +
                "    pcpc_020.REFERENCIA_PECA as referencia, " +
                "    pcpc_040.PROCONF_ITEM as item, " +
                "    basi_140.DESCR_COLECAO, " +
                "    pcpc_040.CODIGO_ESTAGIO, " +
                "    mqop_005.DESCRICAO as descricao_estagio, " +
                "    mqop_005.LEED_TIME as lead_time, " +
                "    pcpc_040.SEQUENCIA_ESTAGIO, " +
                "    pcpc_020.ULTIMO_ESTAGIO, " +
                "    pcpc_040.QTDE_PECAS_PROG as qtde_programada, " +
                "    nvl(PS_FN_CALC_EM_PROD_PACOTE(pcpc_040.periodo_producao, pcpc_040.ordem_confeccao, pcpc_040.codigo_estagio, pcpc_020.ULTIMO_ESTAGIO),0) as qtde_produzir, " +
                "    pcpc_040.QTDE_PECAS_PROD as qtde_produzida, " +
                "    pcpc_040.QTDE_PECAS_2A as qtde_segunda, " +
                "    pcpc_040.QTDE_CONSERTO as qtde_conserto, " +
                "    pcpc_040.QTDE_PERDAS as qtde_perda, " +
                "    ( " +
                "      select " +
                "      count(*) " +
                "      from PSPENDENCIA_OP " +
                "      where (PSPENDENCIA_OP.SIT_PENDENCIA = 'A') " +
                "      and PSPENDENCIA_OP.COD_ORDEMPRODUCAO = pcpc_040.ORDEM_PRODUCAO " +
                "    ) as qtde_pendencia, " +
                "    pcpc_040.CODIGO_FAMILIA as cod_familia, " +
                "    basi_180.DESCRICAO as des_familia, " +
                "    obrf_081.NUMERO_ORDEM as ordem_servico, " +
                "    nvl(obrf_080.DATA_EMISSAO, to_date('19800101','yyyymmdd')) as data_emissao, " +
                "    nvl(obrf_081.DT_PREV_ARECEBER, to_date('19800101','yyyymmdd')) as data_previsao, " +
                "    ( " +
                "      select nvl(max(PSPRORROGACAO_ORDEM_SERVICO.DAT_PRORROGACAO), to_date('19800101','yyyymmdd')) " +
                "      from PSPRORROGACAO_ORDEM_SERVICO " +
                "      where PSPRORROGACAO_ORDEM_SERVICO.NUM_ORDEM = obrf_081.NUMERO_ORDEM " +
                "    ) as data_prorrogacao, " +
                "    supr_010.NOME_FORNECEDOR as nome_terceiro " +
                "    from pcpc_020 " +
                "    join pcpc_040 on (pcpc_020.ORDEM_PRODUCAO = pcpc_040.ORDEM_PRODUCAO) " +
                "    left join mqop_005 on (pcpc_040.CODIGO_ESTAGIO = mqop_005.CODIGO_ESTAGIO) " +
                "    join basi_030 on (basi_030.NIVEL_ESTRUTURA = pcpc_040.PROCONF_NIVEL99 and basi_030.REFERENCIA = pcpc_040.PROCONF_GRUPO) " +
                "    join basi_140 on (basi_140.COLECAO = basi_030.COLECAO) " +
                "    left join obrf_081 on (obrf_081.NUMERO_ORDEM = PS_GET_ORDEM_SERVICO_OP(pcpc_040.ORDEM_PRODUCAO, pcpc_040.ORDEM_CONFECCAO, pcpc_040.CODIGO_ESTAGIO) " +
                "          and obrf_081.PRODORD_NIVEL99 = pcpc_040.PROCONF_NIVEL99 " +
                "          and obrf_081.PRODORD_GRUPO = pcpc_040.PROCONF_GRUPO " +
                "          and obrf_081.PRODORD_SUBGRUPO = pcpc_040.PROCONF_SUBGRUPO " +
                "          and obrf_081.PRODORD_ITEM = pcpc_040.PROCONF_ITEM) " +
                "    left join obrf_080 on (obrf_080.NUMERO_ORDEM = obrf_081.NUMERO_ORDEM) " +
                "    left join supr_010 on (supr_010.FORNECEDOR9 = obrf_080.CGCTERC_FORNE9 " +
                "          and supr_010.FORNECEDOR4 = obrf_080.CGCTERC_FORNE4 " +
                "          and supr_010.FORNECEDOR2 = obrf_080.CGCTERC_FORNE2) " +
                "    left join basi_180 on (pcpc_040.CODIGO_FAMILIA = basi_180.DIVISAO_PRODUCAO) " +
                "    where pcpc_020.COD_CANCELAMENTO = 0 " +
                "    and pcpc_040.PERIODO_PRODUCAO > 1700 and pcpc_020.PERIODO_PRODUCAO > 1700 " +
                "    and pcpc_020.ordem_principal = :ordem_principal " +
                ") ordpend " +
                "having sum(qtde_produzir) - sum(qtde_produzida) - sum(qtde_segunda) - sum(qtde_perda) > 0 " +
                "group by ordpend.ordem_producao, " +
                "         ordpend.tipo_ordem, " +
                "         ordpend.periodo_producao, " +
                "         ordpend.codigo_estagio, " +
                "         ordpend.descr_colecao, " +
                "         ordpend.nome_terceiro, " +
                "         ordpend.descricao_estagio, " +
                "         ordpend.lead_time, " +
                "         ordpend.referencia, " +
                "         ordpend.cod_familia, " +
                "         ordpend.des_familia, " +
                "         ordpend.ordem_servico, " +
                "         ordpend.data_emissao, " +
                "         ordpend.data_previsao, " +
                "         ordpend.data_prorrogacao, " +
                "         ordpend.qtde_pendencia, " +
                "         ordpend.data_prorrogacao, " +
                "         ordpend.nome_terceiro, " +
                "         ordpend.item, " +
                "         ordpend.periodo_alterado, " +
                "         ordpend.ultimo_estagio, " +
                "         ordpend.ROTEIRO_PECA, " +
                "         ordpend.ALTERNATIVA_PECA " +
                "order by ordpend.tipo_ordem desc,ordpend.periodo_producao"
        val mapa = HashMap<String, Any>()
        mapa["ordem_principal"] = ordemProducao

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> OrdemFilhasData(rs.getInt("periodo_producao"), rs.getInt("ordem_producao"),
                rs.getString("grupo"), rs.getString("item"), rs.getString("descricao_estagio"),
                rs.getInt("qtde_programada"), rs.getInt("qtde_produzir"), rs.getInt("qtde_produzida"),
                rs.getInt("qtde_segunda"), rs.getInt("qtde_perda"), rs.getInt("qtde_conserto"),
                rs.getInt("qtde_pendente"), rs.getInt("ordem_servico"), rs.getString("nome_terceiro"))
        }.orEmpty()
    }

    fun getOrdensFilhas(ordemProducao: Int): List<OrdemProducaoData> {
        val sql = "select pcpc_020.ordem_producao, pcpc_020.ordem_principal " +
                "from pcpc_020 " +
                "where pcpc_020.ordem_principal = :ordemProducao " +
                "union " +
                "select pcpc_020.ordem_producao, pcpc_020.ordem_principal " +
                "from pcpc_020 " +
                "where pcpc_020.ordem_producao = :ordemProducao"
        val mapa = HashMap<String, Any>()
        mapa["ordemProducao"] = ordemProducao
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> OrdemProducaoData(rs.getInt("ordem_producao"),
                rs.getInt("ordem_principal"))
        }.orEmpty()
    }

    fun getHasRolosAlocados(ordens: List<Int>): Boolean {
        val sql = "select count(1) as rolos from tmrp_141 " +
                "where tmrp_141.ordem_producao in (:ordens) " +
                "and tmrp_141.codigo_rolo > 0"
        val mapa = HashMap<String, Any>()
        mapa["ordens"] = ordens
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getInt("rolos")
        }.first() > 0
    }

    fun getHasProgramacaoAndSeparacao(ordens: List<Int>): Boolean {
        val sql = "select count(1) estagios from pcpc_040 " +
                "where pcpc_040.ordem_producao in (:ordens) " +
                "and pcpc_040.qtde_pecas_prod > 0 " +
                "and not (pcpc_040.codigo_estagio in (15, 18))"
        val mapa = HashMap<String, Any>()
        mapa["ordens"] = ordens
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getInt("estagios")
        }.first() > 0
    }

    fun cancelarOrdem(ordemProducao: List<Int>, codigoCancelamento: Int, observacao: String): Int {
        val sql = "update pcpc_020 " +
                "set pcpc_020.situacao = 9, " +
                "pcpc_020.cod_cancelamento = :codigoCancelamento, " +
                "pcpc_020.dt_cancelamento = sysdate, " +
                "pcpc_020.observacao2 = :observacao " +
                "where pcpc_020.ordem_producao in (:ordemProducao)"
        val mapa = HashMap<String, Any>()
        mapa["codigoCancelamento"] = codigoCancelamento
        mapa["ordemProducao"] = ordemProducao
        mapa["observacao"] = observacao

        return jdbcTemplate.update(sql, mapa)
    }

    fun getFoo(): List<LocalizadorResultData> {
        val sql = "select estq_300_estq_310.nivel_estrutura, estq_300_estq_310.grupo_estrutura, " +
                "       estq_300_estq_310.subgrupo_estrutura, estq_300_estq_310.item_estrutura, " +
                "       estq_300_estq_310.numero_documento, estq_300_estq_310.sequencia_insercao " +
                "from estq_300_estq_310"
        return jdbcTemplate.query(sql) {
            rs, _ -> LocalizadorResultData(rs.getString("nivel_estrutura"), rs.getString("grupo_estrutura"),
                rs.getString("subgrupo_estrutura"), rs.getString("item_estrutura"),
                qtde_areceber = rs.getInt("numero_documento"), qtde_reservado = rs.getInt("sequencia_insercao"),
                complemento = null, descricao = null, qtde_estq_global = null, qtde_estq_tmrp = null)
        }.orEmpty()
    }
}