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

    fun getTecidoData(nivel: String, grupo: String, sub: String, item: String): TecidoData? {
        val sql = getProdutoDataSql(nivel, grupo, sub, item)
        return jdbcTemplate.query(sql.first, sql.second) {
            rs, _ ->
                val data = TecidoData()
                data.descricao = rs.getString("descricao")
                data.rendimento = rs.getBigDecimal("RENDIMENTO")
                data.unidadeMedida = rs.getString("UNIDADE_MEDIDA")
                data.gramatura1 = rs.getBigDecimal("GRAMATURA_1")
                data.largura1 = rs.getBigDecimal("LARGURA_1")
                data.artigoCota = rs.getString("artigo_cota")
                data.descrCtEstoque = rs.getString("DESCR_CT_ESTOQUE")
                data.complemento = rs.getString("COMPLEMENTO")
                data.nivel = nivel
                data.grupo = grupo
                data.subGrupo = sub
                data.item = item
                data
        }.firstOrNull()
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

        builder.append("Custo informado: R$ ${custo.setScale(2, BigDecimal.ROUND_UP)} + Kg | ")
        if(data.largura1 != null && data.largura1!! > BigDecimal.ZERO
                && data.gramatura1 != null && data.rendimento!! > BigDecimal.ZERO){
            val m2 = (custo/data.rendimento!!) / data.largura1!!
            builder.append("R$ ${m2.setScale(2, BigDecimal.ROUND_UP)}, m2")
            builder.appendln()
        } else {
            builder.append("largura ou rendimento está ZERO.")
        }

        val tmprData = getEstoqueTmrp(
                data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        builder.appendln()
        val estoqueTmrpCalculado = tmprData.first * data.rendimento!!
        builder.append("Estoque TMRP: ${tmprData.first.setScale(2, BigDecimal.ROUND_UP)} ")
        builder.append("${tmprData.second}    |    ")
        builder.append("${estoqueTmrpCalculado.setScale(2, BigDecimal.ROUND_UP)} metros    |    ")
        builder.append("Depositos: ${tmprData.third}")

        val estqData = getEstoque(
                data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        builder.appendln()
        val estoqueCalculado = estqData.first * data.rendimento!!
        builder.append("Estoque: ${estqData.first.setScale(2, BigDecimal.ROUND_UP)} ")
        builder.append("${estqData.second}    |    ")
        builder.append("${estoqueCalculado.setScale(2, BigDecimal.ROUND_UP)} metros    |    ")
        builder.append("Depositos: ${estqData.third}")
        val estoquesReservados = getEstoqueReservado(
                data.nivel.orEmpty(), data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        builder.appendln()
        builder.append("A Receber: ${estoquesReservados.first?.setScale(2, BigDecimal.ROUND_UP)} ${data.unidadeMedida.orEmpty()} | ")
        builder.append("${(estoquesReservados.first?.multiply(data.rendimento!!))?.setScale(2, BigDecimal.ROUND_UP)} metros")
        builder.appendln()
        builder.append("Reservado: ${estoquesReservados.second?.setScale(2, BigDecimal.ROUND_UP)} ${data.unidadeMedida.orEmpty()} | ")
        builder.append("${(estoquesReservados.second?.multiply(data.rendimento!!))?.setScale(2, BigDecimal.ROUND_UP)} metros")

        return builder.toString()
    }
}