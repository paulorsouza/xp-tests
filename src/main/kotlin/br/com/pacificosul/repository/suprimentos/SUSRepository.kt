package br.com.pacificosul.repository.suprimentos

import br.com.pacificosul.data.suprimentos.SUSData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class SUSRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun solicitacaoUrgente(nivel: String?, grupo: String?, subgrupo: String?, item: String?,
                           ordemProducao: Int?, nomeFornecedor: String?, situacoes: List<Int>?): List<SUSData> {
        val stringBuilder = StringBuilder()
        val sql = "select ps_tb_solicitacao_suprimento.*, " +
                "case " +
                "  when ps_tb_solicitacao_suprimento.sit_solicitacao = 0 " +
                "    then 'Solicitado pelo Almoxarifado' " +
                "  when ps_tb_solicitacao_suprimento.sit_solicitacao = 1 " +
                "    then 'Verificado pelo Compras' " +
                "  when ps_tb_solicitacao_suprimento.sit_solicitacao = 2 " +
                "    then 'Atendido no Almoxarifado' " +
                "  when ps_tb_solicitacao_suprimento.sit_solicitacao = 3 " +
                "    then 'Solicitacao Cancelada' " +
                "end des_situacao, " +
                "(basi_030.descr_referencia ||'/'|| basi_020.descr_tam_refer ||'/'||basi_010.DESCRICAO_15) as descricao, " +
                " basi_010.complemento, ps_tb_usuario.des_usuario " +
                " from ps_tb_solicitacao_suprimento " +
                "left join basi_010 on (ps_tb_solicitacao_suprimento.COD_NIVEL = basi_010.NIVEL_ESTRUTURA and " +
                "        ps_tb_solicitacao_suprimento.COD_GRUPO = basi_010.GRUPO_ESTRUTURA and " +
                "        ps_tb_solicitacao_suprimento.COD_SUBGRUPO = basi_010.SUBGRU_ESTRUTURA and " +
                "        ps_tb_solicitacao_suprimento.COD_ITEM = basi_010.ITEM_ESTRUTURA) " +
                "left join basi_020 on (basi_010.NIVEL_ESTRUTURA = basi_020.BASI030_NIVEL030 " +
                "         and BASI_010.GRUPO_ESTRUTURA = basi_020.BASI030_REFERENC and basi_010.SUBGRU_ESTRUTURA = basi_020.TAMANHO_REF) " +
                "left join basi_030 on (basi_030.NIVEL_ESTRUTURA = ps_tb_solicitacao_suprimento.COD_NIVEL " +
                "and basi_030.REFERENCIA = ps_tb_solicitacao_suprimento.COD_GRUPO) " +
                "left join pacificosul.ps_tb_usuario on (ps_tb_solicitacao_suprimento.cod_usuario = ps_tb_usuario.cod_usuario) " +
                "where cod_solicitacao <> 0 "
        stringBuilder.append(sql)
        val mapa = HashMap<String, Any>()
        if (!situacoes.orEmpty().isEmpty()) {
            stringBuilder.append("and sit_solicitacao in (:situacoes) ")
            mapa["situacoes"] = situacoes.orEmpty()
        }
        if (!nivel.isNullOrEmpty()){
            stringBuilder.append("and cod_nivel = :nivel ")
            mapa["nivel"] = nivel.orEmpty()
        }
        if (!grupo.isNullOrEmpty()) {
            stringBuilder.append("and cod_grupo = :grupo ")
            mapa["grupo"] = grupo.orEmpty()
        }
        if (!subgrupo.isNullOrEmpty()) {
            stringBuilder.append("and cod_subgrupo = :subgrupo ")
            mapa["subgrupo"] = subgrupo.orEmpty()
        }
        if (!item.isNullOrEmpty()) {
            stringBuilder.append("and cod_item = :item ")
            mapa["item"] = item.orEmpty()
        }
        if (ordemProducao !== null) {
            stringBuilder.append("and num_op = :ordemProducao ")
            mapa["ordemProducao"] = ordemProducao
        }
        if (!nomeFornecedor.isNullOrEmpty()) {
            stringBuilder.append("and nom_fornecedor like :fornecedor")
            mapa["fornecedor"] = "%"+nomeFornecedor.orEmpty().toUpperCase()+"%"
        }
        System.out.println(stringBuilder.toString())
        System.out.println(mapa.keys)
        System.out.println(mapa.values)
        return jdbcTemplate.query(stringBuilder.toString(), mapa) {
            rs, _ -> SUSData(rs.getInt("cod_solicitacao"), rs.getString("des_situacao"),
                rs.getString("des_usuario"), rs.getDate("dat_corte"), rs.getDate("dat_abertura"),
                rs.getString("cod_referencia"), rs.getString("cod_nivel"), rs.getString("cod_grupo"),
                rs.getString("cod_subgrupo"), rs.getString("cod_item"), rs.getString("descricao"),
                rs.getString("complemento"), rs.getString("cod_medida"), rs.getBigDecimal("qtd_solicitada"),
                rs.getInt("num_op"), rs.getInt("num_oc"), rs.getDate("dat_corte"),
                rs.getString("nom_fornecedor_oc"), rs.getBigDecimal("qtd_pedido_oc"), rs.getInt("num_periodo"),
                rs.getDate("dat_previsao1"), rs.getDate("dat_previsao2"), rs.getDate("dat_previsao3"),
                rs.getString("nom_fornecedor"), rs.getDate("dat_ultimacompra"), rs.getBigDecimal("qtd_ultimacompra"),
                rs.getBigDecimal("val_ultimacompra"))
        }.orEmpty()
    }
}