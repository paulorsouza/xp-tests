package br.com.pacificosul.repository.produto

import br.com.pacificosul.data.produto.PecaData
import br.com.pacificosul.data.produto.ProdutoData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PecaRepository: ProdutoRepository {
    constructor(jdbcTemplate: NamedParameterJdbcTemplate) : super(jdbcTemplate)

    fun getPecaData(referencia: String): PecaData? {
        val sql = "select a.REFERENCIA,a.DESCR_REFERENCIA,b.DESCR_COLECAO,c.DESCR_ARTIGO as artigo_produto, " +
                  "d.DESCR_ARTIGO as artigo_cota, f.DESCR_CT_ESTOQUE, e.DESCR_SERIE from basi_030 a " +
                  "left join basi_140 b on (b.COLECAO = a.COLECAO) " +
                  "left join basi_290 c on (c.ARTIGO = a.ARTIGO) " +
                  "left join basi_295 d on (d.ARTIGO_COTAS = a.ARTIGO_COTAS) " +
                  "left join basi_210 e on (e.SERIE_TAMANHO = a.SERIE_TAMANHO) " +
                  "left join basi_150 f on (f.conta_estoque = a.conta_estoque)" +
                  "where a.REFERENCIA = :referencia " +
                  "  and a.NIVEL_ESTRUTURA = '1' "

        val mapa = HashMap<String, Any>()
        mapa["referencia"] = referencia

        return jdbcTemplate.query(sql, mapa) {
            rs, _ ->
                val data = PecaData()
                data.referencia = rs.getString("REFERENCIA")
                data.descrReferencia = rs.getString("DESCR_REFERENCIA")
                data.descrColecao = rs.getString("DESCR_COLECAO")
                data.artigoProduto = rs.getString("artigo_produto")
                data.artigoCota = rs.getString("artigo_cota")
                data.descrCtEstoque = rs.getString("DESCR_CT_ESTOQUE")
                data.descrSerie = rs.getString("DESCR_SERIE")
                data
        }.firstOrNull()
    }

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

