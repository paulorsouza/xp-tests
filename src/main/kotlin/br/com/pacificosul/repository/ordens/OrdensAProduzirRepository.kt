package br.com.pacificosul.repository.ordens

import br.com.pacificosul.data.ordens.OrdensAProduzirData
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class OrdensAProduzirRepository(private val jdbcTemplate: JdbcTemplate) {
    fun get(listaPeriodo: List<Int>, listaEstagios: List<Int>): List<OrdensAProduzirData> {

        val sql = "select consulta.*, " +
        " trunc((consulta.MINUTO_COSTURA * consulta.QTDE_PROGRAMADA)/480) as dias_costura, " +

        "(case when CODIGO_ESTAGIO = 15 then " +
        "decode((select count(QTDE_RESERVADA) from TMRP_041 a " +
        "  where a.NIVEL_ESTRUTURA = '2' " +
        "  and QTDE_RESERVADA < 0.00005 " +
        "  and a.NR_PEDIDO_ORDEM = consulta.ordem_producao),0,'','SIM') end) as reserva_zerada, " +


        "(case when consulta.tempo_desde_estagio <= consulta.LEED_TIME then 'No Prazo' " +
        "      when consulta.tempo_desde_estagio > consulta.LEED_TIME then 'Atrazo Lead' " +
        "      when consulta.tempo_desde_estagio > 20 then 'Crítico' end) as situacao from " +

        "(select consulta_base.ORDEM_PRODUCAO,consulta_base.ORDEM_PRINCIPAL, consulta_base.CODIGO_ESTAGIO, " +
        " '(' || consulta_base.CODIGO_ESTAGIO || ') ' ||d.DESCRICAO as DesCod_estagio, " +
        " d.DESCRICAO as descricao_estagio, consulta_base.PERIODO_PRODUCAO,d.leed_time, " +
        "consulta_base.tipo_ordem, consulta_base.des_tipo_ordem,consulta_base.tem_sus, " +
        "j.DATA_EMISSAO as data_emissao_os,(select max(x.DT_PREV_ARECEBER) from obrf_081 x" +
        " where x.NUMERO_ORDEM = consulta_base.ordem_servico_estagio) as data_previsao_os, " +
        "consulta_base.REFERENCIA_PECA, consulta_base.PROCONF_ITEM, c.DESCR_COLECAO, op_uti, " +
        "tem_lavacao,ordem_servico_costura, f.NOME_FORNECEDOR as QUEM_COSTUROU, " +
        "g.des_periodo, ordem_servico_estagio, k.NOME_FORNECEDOR as nome_servico_estagio, " +
        "m.DESCR_ARTIGO as artigo_cota,max(o.DAT_PRORROGACAO) as data_prorrogacao_os, " +
        "decode(g.ordenacao,0,99,g.ordenacao) as prioridade, h.ORDEM_MARCA, consulta_base.passou_ultimo_estagio, " +
        "consulta_base.minuto_costura,max(q.DESCRICAO) as carga_programada,s.des_apelido as responsavel, " +
        "cast((select count(1) from pcpc_020 x " +
        " where x.ORDEM_PRINCIPAL = consulta_base.ORDEM_PRODUCAO " +
        " and x.COD_CANCELAMENTO = 0) as number(3)) as qtd_filhos, " +
        "(select case when x.COD_CANCELAMENTO > 0 then 'Cancelado' " +
        "else '' end from pcpc_020 a " +
        "left join pcpc_020 x on (x.ORDEM_PRODUCAO = a.ORDEM_PRINCIPAL) " +
        "where a.ORDEM_PRODUCAO = consulta_base.ORDEM_PRINCIPAL) as pai_cancelado, " +

        "cast('' as char(40)) as op_mesma_ref_cor, " +
        "cast('' as char(40)) as cancelou_op, " +

        "substr(c.descr_colecao,3,7) as estacao, substr(c.descr_colecao,1,2) as marca,consulta_base.CODIGO_FAMILIA, i.DESCRICAO as des_familia, " +
        "consulta_base.PERIODO_PRODUCAO || ' - ' || g.DES_PERIODO as cod_des_periodo, " +
        " case when consulta_base.CODIGO_FAMILIA = 0 then '' " +
        "else " +
        "consulta_base.CODIGO_FAMILIA || ' - ' || i.DESCRICAO end as des_codfamilia, " +

        "cast((select y.DESCRICAO_AGRUPADOR from pacificosul.PS_TB_ESTAGIO_AGRUPADO_ITEM x " +
        "left join pacificosul.ps_tb_estagio_agrupado y on (y.COD_AGRUPADO = x.COD_AGRUPADO) " +
        "where x.CODIGO_ESTAGIO = consulta_base.codigo_estagio and x.COD_VISAO = 13) as varchar2(80)) as descricao_agrupador, " +

        "(case when consulta_base.passou_ultimo_estagio > 0 then " +
        "          (select lpad(round(sysdate-max(x.DATA_PRODUCAO)),3,'0')|| ' - ' || max(to_char(x.DATA_PRODUCAO,'dd/mm/yy'))  from pcpc_045 x " +
        "           where x.ORDEM_PRODUCAO = consulta_base.ORDEM_PRODUCAO " +
        "           and x.PCPC040_ESTCONF = consulta_base.ultimo_estagio) " +
        "    end) as data_prim_baixa_ult_est, " +

        "(case when consulta_base.passou_ultimo_estagio > 0 then 'SIM' else 'NAO' end) as des_passou_ultimo_estagio, " +
        "(case when op_uti = 1 then 'SIM' else 'NAO' end) as des_op_uti, " +
        "(case when tem_lavacao = 1 then 'SIM' else 'NAO' end) as des_tem_lavacao, " +

        "sum(qtde_em_producao_pacote) as qtde_pendente, " +
        "sum(QTDE_PECAS_PROG) as qtde_programada, sum(QTDE_A_PRODUZIR_PACOTE) as qtde_produzir, " +
        "sum(QTDE_PECAS_PROD) as qtde_produzida, sum(QTDE_PECAS_2A) as qtde_segunda, sum(QTDE_CONSERTO) as qtde_conserto, " +
        "sum(QTDE_PERDAS) as qtde_perda,l.TIPO as dificuldade, " +

        "(case when l.TIPO = 1 then 'BAIXO' " +
        "      when l.TIPO = 2 then 'ALTO' " +
        "      when l.TIPO = 3 then 'MEDIO' " +
        "      when l.TIPO = 4 then 'SARJA/JEANS' " +
        "      when l.TIPO = 5 then 'SEM CATEGORIA' " +
        "      end) as des_dificuldade, " +

        "nvl(PS_GET_TEMPO_DESDE_ESTAGIO(consulta_base.ORDEM_PRODUCAO, consulta_base.CODIGO_ESTAGIO),0) as tempo_desde_estagio, " +


        "PACIFICOSUL.PS_FN_SEM_ROT_COSTURA('1',consulta_base.REFERENCIA_PECA) as sem_roteiro_costura,t.DESCR_SERIE " +

        "from ( " +

        "select a.ORDEM_PRODUCAO,b.ORDEM_PRINCIPAL, " +
        "       a.CODIGO_ESTAGIO, b.PERIODO_PRODUCAO, b.REFERENCIA_PECA, a.PROCONF_ITEM,b.ultimo_estagio, " +
        "a.qtde_em_producao_pacote, a.QTDE_PECAS_PROG, a.QTDE_A_PRODUZIR_PACOTE, a.QTDE_PECAS_PROD, a.QTDE_PECAS_2A, a.QTDE_CONSERTO, a.QTDE_PERDAS, " +
        "b.tipo_ordem, " +
        "(case " +
        "when b.tipo_ordem = 2 then 'MOSTRUARIO' " +
        "when b.tipo_ordem = 3 then 'VARIANTE' end) des_tipo_ordem, " +

        "nvl((select 1 from pacificosul.ps_tb_op_uti x where x.ordem_producao = a.ORDEM_PRODUCAO " +
        "and x.sit_ativo = 1),0) as op_uti, " +

        "(case when nvl((select max(CODIGO_ESTAGIO) from pcpc_040 x " +
        "where x.CODIGO_ESTAGIO = 79 " +
        "and x.ORDEM_PRODUCAO = a.ORDEM_PRODUCAO),0) > 0 then 1 else 0 end) as tem_lavacao, " +

        "(select max(x.NUMERO_ORDEM) from pcpc_040 x " +
        "where x.ORDEM_PRODUCAO = a.ORDEM_PRODUCAO " +
        "and x.CODIGO_ESTAGIO = 64) as ordem_servico_costura, " +

        "PS_GET_ORDEM_SERVICO_OP(a.ORDEM_PRODUCAO, a.ORDEM_CONFECCAO, a.CODIGO_ESTAGIO) as ordem_servico_estagio, " +

        "(nvl((select max(1) from pcpc_040 x " +
        "where x.CODIGO_ESTAGIO = b.ULTIMO_ESTAGIO " +
        "and x.QTDE_PECAS_PROD > 0 " +
        "and x.QTDE_A_PRODUZIR_PACOTE > 0 " +
        "and x.ORDEM_PRODUCAO = a.ORDEM_PRODUCAO),0) " +
        ") as passou_ultimo_estagio, " +

        "nvl((select max(x.MINUTO) from pacificosul.PS_VW_CUSTO_MINUTO x " +
        "where x.GRUPO_ESTRUTURA = a.PROCONF_GRUPO " +
        "and (x.ITEM_ESTRUTURA = a.PROCONF_ITEM or x.ITEM_ESTRUTURA  = '000000') " +
        "and x.NUMERO_ROTEIRO = b.ROTEIRO_PECA " +
        "and x.NUMERO_ALTERNATI = b.ALTERNATIVA_PECA " +
        "and x.codigo_estagio = 63),0) as minuto_costura, a.CODIGO_FAMILIA, " +

        "nvl((select max('SIM') from PS_TB_SOLICITACAO_SUPRIMENTO uti " +
        " where uti.num_op = a.ORDEM_PRODUCAO " +
        " and uti.SIT_SOLICITACAO < 2),'NAO') as tem_sus " +

        "from pcpc_040 a " +
        "left join pcpc_020 b on (b.ORDEM_PRODUCAO = a.ORDEM_PRODUCAO) " +
        "where b.COD_CANCELAMENTO = 0 " +
        "and EXISTS(select 1 from pcpc_040 x where x.QTDE_A_PRODUZIR_PACOTE > 0 " +
        "           and x.ORDEM_PRODUCAO = b.ordem_producao and x.CODIGO_ESTAGIO = a.CODIGO_ESTAGIO) " +

        "and a.CODIGO_ESTAGIO in (:listaEstagios) " +
        "and b.PERIODO_PRODUCAO in (:listaPeriodos) " +

        ") consulta_base " +
        "left join basi_030 b on (b.NIVEL_ESTRUTURA = 1 and b.REFERENCIA = consulta_base.REFERENCIA_PECA) " +
        "left join basi_140 c on (c.COLECAO = b.colecao) " +
        "left join mqop_005 d on (d.CODIGO_ESTAGIO = consulta_base.CODIGO_ESTAGIO) " +

        "left join obrf_080 e on (e.NUMERO_ORDEM = consulta_base.ordem_servico_costura) " +
        "left join supr_010 f on (f.FORNECEDOR9 = e.CGCTERC_FORNE9 and f.FORNECEDOR4 = e.CGCTERC_FORNE4 and f.FORNECEDOR2 = e.CGCTERC_FORNE2) " +
        "left join ps_tb_periodo_agrupado g on (g.PERIODO1 = consulta_base.PERIODO_PRODUCAO) " +
        "left join pacificosul.PS_TB_PRIORIDADE_MARCA h on (h.periodo_producao = consulta_base.periodo_producao and h.marca = substr(c.DESCR_COLECAO,1,2)) " +
        "left join basi_180 i on (i.DIVISAO_PRODUCAO = consulta_base.CODIGO_FAMILIA) " +

        "left join obrf_080 j on (j.NUMERO_ORDEM = consulta_base.ordem_servico_estagio) " +
        "left join supr_010 k on (k.FORNECEDOR9 = j.CGCTERC_FORNE9 and k.FORNECEDOR4 = j.CGCTERC_FORNE4 and k.FORNECEDOR2 = j.CGCTERC_FORNE2) " +
        "left join pacificosul.PS_TB_REFERENCIA_TIPO l on (l.NIVEL = '1' and  l.GRUPO = consulta_base.REFERENCIA_PECA) " +
        "left join basi_295 m on (m.ARTIGO_COTAS = b.artigo_cotas) " +
        "left join PSPRORROGACAO_ORDEM_SERVICO o on (o.NUM_ORDEM = j.NUMERO_ORDEM) " +
        "left join ps_tb_carga_faccao p on (p.ORDEM_PRODUCAO = consulta_base.ordem_producao) " +
        "left join basi_180 q on (q.DIVISAO_PRODUCAO = p.DIVISAO_PRODUCAO) " +
        "left join pacificosul.PS_TB_ESTAGIO_USUARIO r on (r.CODIGO_ESTAGIO = consulta_base.CODIGO_ESTAGIO and r.TIPO_USUARIO = 1) " +
        "left join pacificosul.ps_tb_usuario s on (s.COD_USUARIO = r.COD_USUARIO) " +
        "left join basi_210 t on (t.SERIE_TAMANHO = b.SERIE_TAMANHO) " +
        "having sum(qtde_em_producao_pacote) > 0 " +
        "group by t.DESCR_SERIE,consulta_base.ORDEM_PRODUCAO,consulta_base.ORDEM_PRINCIPAL, PROCONF_ITEM, consulta_base.CODIGO_ESTAGIO, consulta_base.PERIODO_PRODUCAO,m.DESCR_ARTIGO,consulta_base.tem_sus, " +
        "REFERENCIA_PECA,tipo_ordem, des_tipo_ordem,c.DESCR_COLECAO, op_uti, d.DESCRICAO, tem_lavacao,ordem_servico_costura,ordem_servico_estagio, k.NOME_FORNECEDOR, " +
        "f.NOME_FORNECEDOR, g.ORDENACAO,g.des_periodo,h.ORDEM_MARCA,consulta_base.passou_ultimo_estagio,consulta_base.minuto_costura,l.TIPO,j.DATA_EMISSAO, " +
        "consulta_base.CODIGO_FAMILIA,i.DESCRICAO,d.LEED_TIME,s.des_apelido,consulta_base.ultimo_estagio " +
        ") consulta "
        //"order by ' + orderBy);
                
        var namedJdbc = NamedParameterJdbcTemplate(jdbcTemplate.dataSource)
        val mapa = HashMap<String, Any>()
        mapa.set("listaEstagios",listaEstagios)
        mapa.set("listaPeriodos",listaPeriodo)

        return namedJdbc.query(sql, mapa) {
            rs, _ -> OrdensAProduzirData(rs.getInt("ORDEM_PRODUCAO"),
                rs.getInt("CODIGO_ESTAGIO"),
                rs.getString("DESCRICAO_ESTAGIO"),
                rs.getInt("PERIODO_PRODUCAO"),
                rs.getInt("TIPO_ORDEM"),
                rs.getString("DES_TIPO_ORDEM"),
                rs.getString("REFERENCIA_PECA"),
                rs.getString("PROCONF_ITEM"),
                rs.getString("DESCR_COLECAO"),
                rs.getFloat("OP_UTI"),
                rs.getFloat("TEM_LAVACAO"),
                rs.getString("QUEM_COSTUROU"),
                rs.getString("DES_PERIODO"),
                rs.getFloat("PRIORIDADE"),
                rs.getString("ORDEM_MARCA"),
                rs.getFloat("PASSOU_ULTIMO_ESTAGIO"),
                rs.getFloat("MINUTO_COSTURA"),
                rs.getString("ESTACAO"),
                rs.getString("DES_PASSOU_ULTIMO_ESTAGIO"),
                rs.getString("DES_OP_UTI"),
                rs.getString("DES_TEM_LAVACAO"),
                rs.getFloat("QTDE_PENDENTE"),
                rs.getFloat("QTDE_PROGRAMADA"),
                rs.getFloat("QTDE_PRODUZIR"),
                rs.getFloat("QTDE_PRODUZIDA"),
                rs.getFloat("QTDE_SEGUNDA"),
                rs.getFloat("QTDE_CONSERTO"),
                rs.getFloat("QTDE_PERDA"),
                rs.getInt("CODIGO_FAMILIA"),
                rs.getString("DES_FAMILIA"),
                rs.getFloat("ORDEM_SERVICO_COSTURA"),
                rs.getFloat("ORDEM_SERVICO_ESTAGIO"),
                rs.getString("NOME_SERVICO_ESTAGIO"),
                rs.getString("DESCRICAO_AGRUPADOR"),
                rs.getInt("ORDEM_PRINCIPAL"),
                rs.getFloat("TEMPO_DESDE_ESTAGIO"),
                rs.getString("DES_CODFAMILIA"),
                rs.getString("COD_DES_PERIODO"),
                rs.getString("DIFICULDADE"),
                rs.getString("DES_DIFICULDADE"),
                rs.getFloat("DIAS_COSTURA"),
                rs.getString("ARTIGO_COTA"),
                rs.getString("TEM_SUS"),
                rs.getString("SITUACAO"),
                rs.getString("DATA_EMISSAO_OS"),
                rs.getString("DATA_PREVISAO_OS"),
                rs.getString("DATA_PRORROGACAO_OS"),
                rs.getString("CARGA_PROGRAMADA"),
                rs.getInt("QTD_FILHOS"),
                rs.getString("PAI_CANCELADO"),
                rs.getString("OP_MESMA_REF_COR"),
                rs.getString("CANCELOU_OP"),
                rs.getString("RESPONSAVEL"),
                rs.getString("MARCA"),
                rs.getString("DATA_PRIM_BAIXA_ULT_EST"),
                rs.getString("SEM_ROTEIRO_COSTURA"),
                rs.getString("RESERVA_ZERADA"),
                rs.getString("DESCOD_ESTAGIO"),
                rs.getString("DESCR_SERIE"))
        }.orEmpty()

    }
}