package br.com.pacificosul.repository.produto

import br.com.pacificosul.data.produto.TecidoData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigDecimal

open class TecidoRepository : ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getValorCusto(nivel: String, grupo: String, sub: String, item: String): BigDecimal {
        val sql = "select valor_custo from (select valor_custo from rcnb_075 " +
            "where nivel_produto = :nivel " +
            "  and grupo_produto = :grupo " +
            "  and subgru_produto = :sub " +
            "  and item_produto = :item " +
            "order by data_cadastro desc) " +
            "where rownum = 1 "

        val mapa = HashMap<String, Any>()
        mapa["nivel"] = nivel
        mapa["grupo"] = grupo
        mapa["sub"] = sub
        mapa["item"] = item

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getBigDecimal("valor_custo")
        }.first()
    }

    override fun <T> getText(data: T): String {
        val data = data as TecidoData
        val builder = StringBuilder()
        builder.append("Descrição: ${data.descrReferencia.orEmpty()}")
        if(!data.complemento.isNullOrBlank())
            builder.append("Complemento: ${data.complemento}")
        builder.appendln()
        builder.append("Artigo Cota: ${data.artigoCota.orEmpty()}")
        builder.append(" | Conta de Estoque: ${data.descrCtEstoque.orEmpty()}")
        builder.appendln()

        val custo = getValorCusto(
               data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )

        builder.append("Custo informado: R$ ${custo.setScale(2)} + Kg | ")
        if(data.largura1 != null && data.largura1!! > BigDecimal.ZERO
                && data.gramatura1 != null && data.rendimento!! > BigDecimal.ZERO){
            val m2 = (custo/data.rendimento!!) / data.largura1!!
            builder.append("R$ ${m2.setScale(2)}, m2")
            builder.appendln()
        } else {
            builder.append("largura ou rendimento está ZERO.")
        }

        val tmprData = getEstoqueTmrp(
                data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        val estoqueTmrpCalculado = tmprData.first * data.rendimento!!
        builder.append("Estoque TMRP: ${tmprData.first.setScale(2)} ")
        builder.append("${tmprData.second}    |    ")
        builder.append("${estoqueTmrpCalculado.setScale(2)} metros    |    ")
        builder.append("Depositos: ${tmprData.third}")

        val estqData = getEstoque(
                data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        val estoqueCalculado = estqData.first * data.rendimento!!
        builder.append("Estoque: ${estqData.first.setScale(2)} ")
        builder.append("${estqData.second}    |    ")
        builder.append("${estoqueCalculado.setScale(2)} metros    |    ")
        builder.append("Depositos: ${estqData.third}")

        return builder.toString()
    }
}