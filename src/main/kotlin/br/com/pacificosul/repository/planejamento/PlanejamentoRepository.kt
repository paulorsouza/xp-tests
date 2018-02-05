package br.com.pacificosul.repository.planejamento

import br.com.pacificosul.data.planejamento.RolosOrdemData
import br.com.pacificosul.model.Referencia
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PlanejamentoRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {
    fun rolosOrdem(referencia: Referencia, deposito: Int): List<RolosOrdemData> {
        val sql = "select con.PERIODO_PRODUCAO, con.CODIGO_ROLO, con.CODIGO_DEPOSITO, con.NUMERO_LOTE, con.QTDE_QUILOS_ACAB, con.ORDEM_PRODUCAO, con.tip_ordem, con.colecao, con.DATA_RESERVA, " +
                "      ( " +
                "        SELECT pacificosul.ps_fn_estagio_op(con.ORDEM_PRODUCAO) FROM DUAL) as estagio, " +
                "               decode(con.ORDEM_PRODUCAO,null, null, con.ORDEM_PRODUCAO || ' - ' || con.colecao || ' ' ||  + con.tip_ordem) as des_OP " +
                "        from (select pcpc_020.PERIODO_PRODUCAO, pcpt_020_025.CODIGO_ROLO, pcpt_020_025.CODIGO_DEPOSITO, pcpt_020_025.NUMERO_LOTE, pcpt_020_025.QTDE_QUILOS_ACAB, tmrp_141.ORDEM_PRODUCAO, " +
                "                      case  " +
                "                        when pcpc_020.TIPO_ORDEM = 1 then '(P)' " +
                "                        when pcpc_020.TIPO_ORDEM = 2 then '(M)' " +
                "                        when (pcpc_020.TIPO_ORDEM = 3 and pcpc_020.CODIGO_MOTIVO = 1) then '(V)' " +
                "                      end as tip_ordem, " +
                "                    basi_140.DESCR_COLECAO as colecao, tmrp_141.DATA_RESERVA " +
                "              from pcpt_020_025 " +
                "              left join basi_010 on (basi_010.NIVEL_ESTRUTURA = pcpt_020_025.PANOACAB_NIVEL99 and  " +
                "                                     basi_010.GRUPO_ESTRUTURA = pcpt_020_025.PANOACAB_GRUPO and  " +
                "                                     basi_010.SUBGRU_ESTRUTURA = pcpt_020_025.PANOACAB_SUBGRUPO and  " +
                "                                     basi_010.ITEM_ESTRUTURA = pcpt_020_025.PANOACAB_ITEM) " +
                "        left join pcpt_025 on (pcpt_025.CODIGO_ROLO = pcpt_020_025.CODIGO_ROLO) " +
                "        left join pcpt_020 on (pcpt_020.CODIGO_ROLO = pcpt_020_025.CODIGO_ROLO) " +
                "        left join tmrp_141 on (tmrp_141.CODIGO_ROLO = pcpt_020_025.CODIGO_ROLO) " +
                "        left join pcpc_020 on (pcpc_020.ORDEM_PRODUCAO = tmrp_141.ORDEM_PRODUCAO) " +
                "        left join basi_030 on (basi_030.REFERENCIA = pcpc_020.REFERENCIA_PECA AND basi_030.NIVEL_ESTRUTURA = '1') " +
                "        left join basi_140 on (basi_140.COLECAO = basi_030.COLECAO) " +
                "        where pcpt_020_025.ROLO_ESTOQUE in (1,3) " +
                "              and pcpt_020_025.PANOACAB_NIVEL99 = :nivel " +
                "              and pcpt_020_025.PANOACAB_GRUPO = :grupo " +
                "              and pcpt_020_025.PANOACAB_SUBGRUPO = :subgrupo " +
                "              and pcpt_020_025.PANOACAB_ITEM = :item " +
                "              and pcpt_020_025.CODIGO_DEPOSITO = :deposito " +
                "        ) con "

        val mapa = HashMap<String, Any>()
        mapa["nivel"] = referencia.nivel
        mapa["grupo"] = referencia.grupo
        mapa["subgrupo"] = referencia.subgrupo
        mapa["item"] = referencia.item
        mapa["deposito"] = deposito

        return namedParameterJdbcTemplate.query(sql, mapa) {
            rs, _ -> RolosOrdemData(rs.getInt("periodo_producao"), rs.getInt("codigo_rolo"),
                rs.getInt("codigo_deposito"), rs.getString("numero_lote"),
                rs.getBigDecimal("qtde_quilos_acab"), rs.getInt("ordem_producao"),
                rs.getNString("tip_ordem"), rs.getNString("colecao"), rs.getDate("data_reserva"),
                rs.getString("estagio"))
        }.orEmpty()
    }
}