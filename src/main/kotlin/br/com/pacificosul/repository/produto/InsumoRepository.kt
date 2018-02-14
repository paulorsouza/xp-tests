package br.com.pacificosul.repository.produto

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import br.com.pacificosul.data.produto.InsumoData

class InsumoRepository: ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getEndereco(grupo: String, sub: String, item: String): String? {
        val sql = "select max(endereco) as endereco  from estq_110 a " +
                  "where a.nivel = '9' " +
                  "  and a.grupo = :grupo " +
                  "  and a.subgrupo = :sub " +
                  "  and a.item = :item " +
                  "  and a.deposito = 51 " +
                  "group by nivel,grupo "

        val mapa = HashMap<String, Any>()

        mapa["grupo"] = grupo
        mapa["sub"] = sub
        mapa["item"] = item
        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString("endereco")
        }.firstOrNull()
    }

    override fun <T> getText(data: T): String {
        val data = data as InsumoData
        val builder = StringBuilder()
        builder.append("Endereço: ${getEndereco(
                data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        ).orEmpty()}")
        builder.appendln()
        builder.append("Descrição: ${data.descrReferencia.orEmpty()}")
        if(!data.complemento.isNullOrBlank())
            builder.append("Complemento: ${data.complemento}")
        builder.appendln()
        builder.append("Artigo Cota: ${data.artigoCota.orEmpty()}")
        builder.append(" | Conta de Estoque: ${data.descrCtEstoque.orEmpty()}")
        val trmpData = getEstoqueTmrp(
                "9", data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        builder.append("Estoque TMRP: ${trmpData.first.setScale(2)} ")
        builder.append("${trmpData.second}    |   ")
        builder.append("Depositos: ${trmpData.third}")
        val estqData = getEstoque(
                "9", data.grupo.orEmpty(), data.subGrupo.orEmpty(), data.item.orEmpty()
        )
        builder.append("Estoque: ${estqData.first.setScale(2)} ")
        builder.append("${estqData.second}    |   ")
        builder.append("Depositos: ${estqData.third}")
        return builder.toString()
    }
}