package br.com.pacificosul.repository.ordens

import br.com.pacificosul.data.ordens.EstagiosAProduzir200Data
import org.springframework.jdbc.core.JdbcTemplate

class PendenteEstagioRepository(private val jdbcTemplate: JdbcTemplate) {
    fun get(periodosConcatenados: String = ""): List<EstagiosAProduzir200Data> {
        val filtro = if (!periodosConcatenados.isNullOrEmpty()) "AND PCPC_020.PERIODO_PRODUCAO IN (?) " else " "

        val sql = "SELECT PCPC_040.CODIGO_ESTAGIO, MQOP_005.DESCRICAO, TEA.DESCRICAO_AGRUPADOR, TU.DES_APELIDO, TU.NUM_RAMAL, " +
                "  SUM(PCPC_040.QTDE_EM_PRODUCAO_PACOTE) QTDE_A_PROD, " +
                "  COUNT(DISTINCT PCPC_020.ORDEM_PRODUCAO) QTDE_ORDENS " +
                "FROM PCPC_040 " +
                "LEFT JOIN PCPC_020 ON PCPC_040.ORDEM_PRODUCAO = PCPC_020.ORDEM_PRODUCAO " +
                "LEFT JOIN MQOP_005 ON PCPC_040.CODIGO_ESTAGIO = MQOP_005.CODIGO_ESTAGIO " +
                "LEFT JOIN pacificosul.PS_TB_ESTAGIO_USUARIO TEU ON (PCPC_040.CODIGO_ESTAGIO = TEU.CODIGO_ESTAGIO AND TEU.TIPO_USUARIO = 1) " +
                "LEFT JOIN pacificosul.PS_TB_USUARIO TU ON (TEU.COD_USUARIO = TU.COD_USUARIO) " +
                "LEFT JOIN EFIC_050 ON LPAD(TU.COD_USUARIO_VETORH, 7, '0') = EFIC_050.CRACHA_FUNCIONARIO " +
                "LEFT JOIN pacificosul.PS_TB_ESTAGIO_AGRUPADO_ITEM TEAI ON (PCPC_040.CODIGO_ESTAGIO = TEAI.CODIGO_ESTAGIO AND TEAI.COD_VISAO = 13) " +
                "LEFT JOIN pacificosul.PS_TB_ESTAGIO_AGRUPADO TEA ON (TEAI.COD_AGRUPADO = TEA.COD_AGRUPADO) " +
                " " +
                "WHERE PCPC_040.QTDE_EM_PRODUCAO_PACOTE > 0 " +
                "AND PCPC_020.COD_CANCELAMENTO = 0 " +
                "AND MQOP_005.AREA_PRODUCAO = 1 " +
                filtro +
                "GROUP BY PCPC_040.CODIGO_ESTAGIO, MQOP_005.DESCRICAO, TEA.DESCRICAO_AGRUPADOR, TU.DES_APELIDO, TU.NUM_RAMAL " +
                "ORDER BY PCPC_040.CODIGO_ESTAGIO"
        return jdbcTemplate.query(sql) {
            rs, _ -> EstagiosAProduzir200Data(rs.getInt("CODIGO_ESTAGIO"),
                rs.getString("DESCRICAO"), rs.getString("DESCRICAO_AGRUPADOR"), rs.getString("DES_APELIDO"),
                rs.getInt("NUM_RAMAL"), rs.getInt("QTDE_A_PROD"), rs.getInt("QTDE_ORDENS"))
        }.orEmpty()
    }
}