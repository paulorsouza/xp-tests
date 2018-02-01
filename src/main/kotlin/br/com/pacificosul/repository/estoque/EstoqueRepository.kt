package br.com.pacificosul.repository.estoque

import br.com.pacificosul.data.estoque.EstoqueDeposito
import br.com.pacificosul.model.Referencia
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class EstoqueRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {
    fun getEstoquePorDeposito(referencia: Referencia, isTmrp: Boolean = true): List<EstoqueDeposito>
    {
        val sql = "select estq_040.deposito, basi_205.descricao, sum(estq_040.qtde_estoque_atu) as quantidade" +
                " from estq_040" +
                " inner join basi_205 on (estq_040.deposito = basi_205.codigo_deposito)" +
                " where estq_040.cditem_nivel99 = :nivel" +
                " and estq_040.cditem_grupo = :grupo" +
                " and estq_040.cditem_subgrupo = :subgrupo" +
                " and estq_040.cditem_item = :item" +
                " and estq_040.qtde_estoque_atu <> 0"
        if (isTmrp) sql.plus(" and basi_205.considera_trmp = 1")
        sql.plus(" group by estq_040.deposito, basi_205.descricao")

        val mapa = HashMap<String, Any>()
        mapa["nivel"] = referencia.nivel
        mapa["grupo"] = referencia.grupo
        mapa["subgrupo"] = referencia.subgrupo
        mapa["item"] = referencia.item

        return namedParameterJdbcTemplate.query(sql, mapa) {
            rs, _ -> EstoqueDeposito(rs.getInt("deposito"),
                rs.getString("descricao"),
                rs.getBigDecimal("quantidade"))
        }.orEmpty()
    }
}