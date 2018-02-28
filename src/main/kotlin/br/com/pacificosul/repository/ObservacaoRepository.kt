package br.com.pacificosul.repository

import br.com.pacificosul.data.ObservacaoData
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class ObservacaoRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
   fun listAll(ordemProducao: Int): List<ObservacaoData> {
       val sql = "select * from pacificosul.ps_tb_obs_200 " +
               "where ordem_producao = :ordemProducao " +
               "order by data_observacao desc"

       val mapa = HashMap<String, Any>()
       mapa["ordemProducao"] = ordemProducao

       return jdbcTemplate.query(sql, mapa) {
           rs, _ -> ObservacaoData(
               ordemProducao, rs.getString("usuario"), rs.getString("observacao"),
               rs.getInt("sequencia"), rs.getString("estagio"), rs.getDate("data_observacao"))
       }.orEmpty()
   }

    fun addObservacao(nome: String, ordem: Int, observacao: String, descEstagio: String): Int {
//        val estagio = "($estagio)$desEstagio"
        val insert = "insert into PACIFICOSUL.PS_TB_OBS_200 " +
                "(ordem_producao, usuario, observacao, estagio, data_observacao) " +
                "values (:ordemProducao, :usuario, :observacao, :descricaoEstagio, CURRENT_DATE)"
        val mapa = HashMap<String, Any>()
        mapa["ordemProducao"] = ordem
        mapa["usuario"] = nome
        mapa["observacao"] = observacao
        mapa["descricaoEstagio"] = descEstagio
        return jdbcTemplate.update(insert, mapa)
    }


    fun getObservacaoSystextil(numeroOp: Int): Pair<String, String>? {
        val sql = " select pcpc_020.observacao, pcpc_020.observacao2 from pcpc_020 " +
                "where pcpc_020.ordem_producao = :ordemProducao"
        val mapa = HashMap<String, Any>()
        mapa["ordemProducao"] = numeroOp
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> Pair(rs.getString(1), rs.getString(2))
        }.firstOrNull()
    }

    fun getObservacaoPeD(codReferencia: Int): List<ObservacaoData>? {
        val sql = "select ps_tb_movimentacao_obs.dat_obs as data, ps_tb_usuario.des_apelido as apelido, " +
                "ps_tb_fase.nom_fase as fase, ps_tb_movimentacao_obs.obs_movimentacao as obs " +
                "from pacificosul.ps_tb_movimentacao_obs " +
                "left join pacificosul.ps_tb_usuario on (ps_tb_movimentacao_obs.cod_usuario = ps_tb_usuario.cod_usuario) " +
                "left join pacificosul.ps_tb_fase on (ps_tb_movimentacao_obs.cod_fase = ps_tb_fase.cod_fase) " +
                "where cod_referencia = :codReferencia " +
                "order by dat_obs desc "
        val mapa = HashMap<String, Any>()
        mapa["codReferencia"] = codReferencia.toString()
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> ObservacaoData(null, rs.getString("apelido"),
                rs.getString("obs"), 1, rs.getString("fase"), rs.getDate("data"))
        }.orEmpty()
    }

    fun listFromEstagiosParalelos(ordemProducao: Int): List<ObservacaoData> {
        val sql = (""
                + "select * from PACIFICOSUL.PS_TB_OBS_200 a "
                + "where a.ORDEM_PRODUCAO = :ordemProducao "
                + "and exists (select "
                + "  1 "
                + "from ( "
                + "    select "
                + "    pcpc_020.PERIODO_PRODUCAO, "
                + "    decode(pcpc_040.PERIODO_PRODUCAO,pcpc_020.PERIODO_PRODUCAO,0,1) as periodo_alterado, "
                + "    pcpc_040.ORDEM_PRODUCAO, "
                + "    pcpc_020.ROTEIRO_PECA, "
                + "    pcpc_020.ALTERNATIVA_PECA, "
                + "    pcpc_020.TIPO_ORDEM, "
                + "    pcpc_020.ORDEM_PRINCIPAL, "
                + "    pcpc_040.ORDEM_CONFECCAO, "
                + "    pcpc_020.DATA_PROGRAMACAO, "
                + "    pcpc_020.REFERENCIA_PECA as referencia, "
                + "    pcpc_040.PROCONF_ITEM as item, "
                + "    basi_140.DESCR_COLECAO, "
                + "    pcpc_040.CODIGO_ESTAGIO, "
                + "    mqop_005.DESCRICAO as descricao_estagio, "
                + "    mqop_005.LEED_TIME as lead_time, "
                + "    pcpc_040.SEQUENCIA_ESTAGIO, "
                + "    pcpc_020.ULTIMO_ESTAGIO, "
                + "    pcpc_040.QTDE_PECAS_PROG as qtde_programada, "
                + "    nvl(pcpc_040.QTDE_A_PRODUZIR_PACOTE,0) qtde_produzir, "
                + "    pcpc_040.QTDE_PECAS_PROD as qtde_produzida, "
                + "    pcpc_040.QTDE_PECAS_2A as qtde_segunda, "
                + "    pcpc_040.QTDE_CONSERTO as qtde_conserto, "
                + "    pcpc_040.QTDE_PERDAS as qtde_perda, pcpc_040.qtde_em_producao_pacote as qtde_pendente, "
                + "    (   select "
                + "        count(*) "
                + "        from PSPENDENCIA_OP "
                + "        where (PSPENDENCIA_OP.SIT_PENDENCIA = 'A') "
                + "        and PSPENDENCIA_OP.COD_ORDEMPRODUCAO = pcpc_040.ORDEM_PRODUCAO "
                + "    ) as qtde_pendencia, "
                + "    (   select "
                + "        count(*) "
                + "        from PS_TB_SOLICITACAO_SUPRIMENTO uti "
                + "        where uti.num_op = pcpc_040.ordem_producao "
                + "        and uti.COD_NIVEL = pcpc_040.PROCONF_NIVEL99 "
                + "        and uti.COD_GRUPO = pcpc_040.PROCONF_GRUPO "
                + "        and uti.COD_SUBGRUPO = pcpc_040.PROCONF_SUBGRUPO "
                + "        and uti.COD_ITEM = pcpc_040.PROCONF_ITEM "
                + "        and uti.SIT_SOLICITACAO < 2 "
                + "    ) as qtde_uti, "
                + "    pcpc_040.CODIGO_FAMILIA as cod_familia, "
                + "    basi_180.DESCRICAO as des_familia, "
                + "    obrf_081.NUMERO_ORDEM as ordem_servico, "
                + "    nvl(obrf_080.DATA_EMISSAO, to_date('19800101','yyyymmdd')) as data_emissao, "
                + "    nvl(obrf_081.DT_PREV_ARECEBER, to_date('19800101','yyyymmdd')) as data_previsao, "
                + "    (   select nvl(max(PSPRORROGACAO_ORDEM_SERVICO.DAT_PRORROGACAO), to_date('19800101','yyyymmdd')) "
                + "        from PSPRORROGACAO_ORDEM_SERVICO "
                + "        where PSPRORROGACAO_ORDEM_SERVICO.NUM_ORDEM = obrf_081.NUMERO_ORDEM "
                + "    ) as data_prorrogacao, "
                + "    supr_010.NOME_FORNECEDOR as nome_terceiro, basi_030.artigo_cotas, basi_295.descr_artigo "
                + "    from pcpc_040 "
                + "    left join mqop_005 on (pcpc_040.CODIGO_ESTAGIO = mqop_005.CODIGO_ESTAGIO) "
                + "    join basi_030 on (basi_030.NIVEL_ESTRUTURA = pcpc_040.PROCONF_NIVEL99 "
                + "          and basi_030.REFERENCIA = pcpc_040.PROCONF_GRUPO) "
                + "    join basi_140 on (basi_140.COLECAO = basi_030.COLECAO) "
                + "    left join obrf_081 on (obrf_081.NUMERO_ORDEM = PACIFICOSUL.PS_GET_ORDEM_SERVICO_OP(pcpc_040.ORDEM_PRODUCAO, pcpc_040.ORDEM_CONFECCAO, pcpc_040.CODIGO_ESTAGIO) "
                + "                    and obrf_081.PRODORD_NIVEL99 = pcpc_040.PROCONF_NIVEL99 "
                + "                    and obrf_081.PRODORD_GRUPO = pcpc_040.PROCONF_GRUPO "
                + "                    and obrf_081.PRODORD_SUBGRUPO = pcpc_040.PROCONF_SUBGRUPO "
                + "                    and obrf_081.PRODORD_ITEM = pcpc_040.PROCONF_ITEM) "
                + "    left join obrf_080 on (obrf_080.NUMERO_ORDEM = obrf_081.NUMERO_ORDEM) "
                + "    left join supr_010 on (supr_010.FORNECEDOR9 = obrf_080.CGCTERC_FORNE9 "
                + "                    and supr_010.FORNECEDOR4 = obrf_080.CGCTERC_FORNE4 "
                + "                    and supr_010.FORNECEDOR2 = obrf_080.CGCTERC_FORNE2) "
                + "    left join basi_180 on (pcpc_040.CODIGO_FAMILIA = basi_180.DIVISAO_PRODUCAO) "
                + "    join pcpc_020 on (pcpc_040.ORDEM_PRODUCAO = pcpc_020.ORDEM_PRODUCAO) left join basi_295 on (basi_030.artigo_cotas = basi_295.artigo_cotas) "
                + "    where pcpc_020.COD_CANCELAMENTO = 0 "
                + "          and (pcpc_040.ORDEM_PRODUCAO = :ordemProducao) "
                + "          and (pcpc_020.PERIODO_PRODUCAO between 0 and 9999 or pcpc_020.TIPO_ORDEM = 2 or pcpc_020.TIPO_ORDEM = 3) "
                + ") ordpend "
                + "where ordpend.codigo_estagio = substr(a.estagio,2,2) "
                + "having sum(qtde_pendente) > 0) "
                + "order by a.data_observacao desc")

        val mapa = HashMap<String, Any>()
        mapa["ordemProducao"] = ordemProducao

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> ObservacaoData(
                ordemProducao, rs.getString("usuario"), rs.getString("observacao"),
                rs.getInt("sequencia"), rs.getString("estagio"), rs.getDate("data_observacao"))
        }.orEmpty()
    }
}
