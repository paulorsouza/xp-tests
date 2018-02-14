package br.com.pacificosul.repository.produto

import br.com.pacificosul.data.produto.PecaData
import br.com.pacificosul.data.produto.ProdutoData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PecaRepository: ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getEndereco(referencia: String): String? {
        val sql = "select wm_concat(DISTINCT endereco) as enderecos " +
                  "from estq_110 " +
                  "where estq_110.NIVEL = '1' " +
                  "  and estq_110.GRUPO = :referencia " +
                  "  and deposito in (1,65) " +
                  "group by nivel,grupo"

        val mapa = HashMap<String, Any>()
        mapa["referencia"] = referencia

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getString("enderecos")
        }.firstOrNull()
    }

    override fun <T> getText(data: T): String {
        val data = data as PecaData
        val builder = StringBuilder()
        builder.append("Coleção Atual: ${data.descrColecao}")
        builder.append("   Endereço: ${getEndereco(data.referencia.orEmpty())}  ")
        builder.appendln("Descrição: ${data.descrReferencia}")
        builder.appendln("Artigo Produto: ${data.artigoProduto}")
        builder.append(" | Cor Mostruario: ${getCorMostruario(data.referencia.orEmpty())}")
        builder.appendln("Artigo Cota: ${data.artigoCota}")
        builder.append(" | Conta de Estoque: ${data.descrCtEstoque}")
        builder.appendln("Serie de Tamanho: ${data.descrSerie}")
        return builder.toString()
    }
}

