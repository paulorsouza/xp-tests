package br.com.pacificosul.repository

import br.com.pacificosul.data.PedidosRecebidosData
import br.com.pacificosul.data.TotalPedidosRecebidosData
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Date

class TotalPedidosRecebidosRepository(private val jdbcTemplate: JdbcTemplate) {

    fun get(dataFiltro: Date, showPedidosSistema: Boolean, isGrouped: Boolean): List<TotalPedidosRecebidosData> {

        val campoPedido = if (showPedidosSistema) "rep_pedido.num_pedido " else "cast(1 as decimal) "
        val mostrarPedidosSistema = if (showPedidosSistema) {
            " and EXISTS (select 1 from pedido where rep_pedido.`num_pedido` = pedido.`num_pedido` and `rep_pedido`.`cod_representante` = pedido.`cod_representante`) "
        } else {
            " and not EXISTS (select 1 from pedido where rep_pedido.`num_pedido` = pedido.`num_pedido` and `rep_pedido`.`cod_representante` = pedido.`cod_representante`) "
        }

        val groupBy = if (isGrouped) "group by representante.cod_representante " else "group by representante.cod_representante,rep_pedido.cod_periodo "

        val sql = "select $campoPedido as num_pedido,rep_cliente.des_estado, " +
                "       if(representante.sit_representante=0,'Inativo','') as situacao, " +
                "       (select GROUP_CONCAT(`representante_marca`.`cod_marca`) from representante_marca " +
                "       where representante_marca.cod_representante = representante.cod_representante order by `representante_marca`.`cod_marca`) as cod_empresa " +
                "  ,representante.`des_regiao`, " +
                "  `representante`.`cod_representante`,`representante`.`des_apelido` as teste, `representante`.`des_apelido`, sum(rep_pedido.`qtd_pedido`) as qtd_pedido, " +
                "  sum(if (tip_quinbon = 'D' and tip_valbon = 'D', " +
                "          rep_pedido.`vlr_pedido`*((1-val_quinbon/100)*(1-val_valbon/100)),( " +
                "            if (tip_quinbon = 'D',rep_pedido.`vlr_pedido`*(1-val_quinbon/100), ( " +
                "              if(tip_valbon = 'D',rep_pedido.`vlr_pedido`*(1-val_valbon/100),rep_pedido.`vlr_pedido`))  )))) as total, " +
                "  GROUP_CONCAT(DISTINCT(ifnull(des_periodo,'PRONTA ENTREGA'))) as des_periodo, " +
                "  cast(GROUP_CONCAT(DISTINCT(rep_pedido.cod_periodo)) as char(100)) as cod_periodo from rep_pedido " +
                "  left join `rep_cliente` on (`rep_pedido`.`num_documento` = `rep_cliente`.`num_documento`) " +
                "  left join `representante` on (`representante`.`cod_representante` = `rep_pedido`.`cod_representante`) " +
                "  left join periodo on (periodo.cod_periodo = rep_pedido.cod_periodo) " +
                "where `rep_pedido`.`qtd_pedido` > 0 " +
                "      and `rep_pedido`.`dat_recebido` = ? " +
                mostrarPedidosSistema +
                groupBy +
                "order by `representante`.`cod_representante`,`rep_pedido`.`vlr_pedido`, `rep_cliente`.`nom_cliente`"

        return jdbcTemplate.query(sql, arrayOf(dataFiltro)){
            rs, _ ->
            TotalPedidosRecebidosData(rs.getInt("num_pedido"),
                    rs.getString("des_estado"),
                    rs.getString("situacao"),
                    rs.getString("cod_empresa"),
                    rs.getString("des_regiao"),
                    rs.getInt("cod_representante"),
                    rs.getString("des_apelido"),
                    rs.getDouble("qtd_pedido"),
                    rs.getDouble("total"),
                    rs.getInt("cod_periodo"),
                    rs.getString("des_periodo"))
        }.orEmpty()
    }
    fun getPedidos(periodo: String? = null, codRepresentante: Int? = null,
                   dataInicioFiltro: Date? = null, dataTerminoFiltro: Date? = null): List<PedidosRecebidosData> {
        val sql = StringBuilder()
        sql.append("select `representante`.`des_apelido`,cliente.des_estado,representante.cod_representante, ")
        sql.append("`rep_pedido`.`vlr_pedido`,rep_pedido.`dat_recebido`,if(representante.sit_representante=0,'Inativo','') as situacao, ")
        sql.append("rep_pedido.`num_pedido`, ")
        sql.append("`rep_pedido`.`dat_entrega1`, ")
        sql.append("`rep_pedido`.`dat_entrega2`,cliente.num_documento, ")
        sql.append("cliente.`nom_cliente`,cliente.`des_cidade`, num_pedidovinculado, qtd_pedido, ")
        sql.append("if(des_periodo <> '',des_periodo,'PRONTA ENTREGA') as des_periodo, ")
        sql.append("if(rep_pedido.`cod_sistema` = 1, ")
        sql.append("(select if(count(*)<>1,'Não','') as pertence_regiao from regiao a ")
        sql.append("where a.cod_cidade = cliente.cod_cidade ")
        sql.append("and a.`cod_representante` = representante.cod_representante ")
        sql.append("and a.`cod_marca` = periodo.cod_marca), ")
        sql.append("(select if(count(*)=0,'Não','') as pertence_regiao from regiao a ")
        sql.append("where a.cod_cidade = cliente.cod_cidade ")
        sql.append("and a.`cod_representante` = representante.cod_representante) ")
        sql.append(") as pertence_regiao, ")
        sql.append("(select group_concat(DISTINCT des_apelido) as des_apelido from regiao a ")
        sql.append("left join representante b on (b.cod_representante = a.cod_representante) ")
        sql.append("where a.cod_cidade = cliente.cod_cidade ")
        sql.append("and b.`sit_representante` <> 0 ")
        sql.append("and a.`cod_marca` = periodo.cod_marca) as rep_regiao, ")
        sql.append("(select GROUP_CONCAT(a.`cod_marca`) from representante_marca a ")
        sql.append("where a.`cod_representante` = representante.cod_representante) as marcas, ")
        sql.append("(select if(count(*)=1,'Sim','Não') from pedido ")
        sql.append("where pedido.num_pedido = rep_pedido.num_pedido ")
        sql.append("and pedido.cod_representante = rep_pedido.cod_representante) as pedido_integrado ")

        sql.append("from `rep_pedido` ")
        sql.append("left join `cliente` on (`rep_pedido`.`num_documento` = `cliente`.`num_documento`) ")
        sql.append("left join `representante` on (`representante`.`cod_representante` = `rep_pedido`.`cod_representante`) ")
        sql.append("left join periodo on (rep_pedido.cod_periodo = periodo.cod_periodo) ")
        sql.append("where `rep_pedido`.`qtd_pedido` > 0 ")

        val sqlParameters = mutableMapOf<String, Any>()

        if (!periodo.isNullOrEmpty()) {
            sql.append("and rep_pedido.cod_periodo in (?) ")
            sqlParameters.put("periodo", periodo.orEmpty())
        }

        if (codRepresentante != null) {
            sql.append("and rep_pedido.cod_representante = ? ")
            sqlParameters.put("codRepresentante", codRepresentante)
        }

        if (dataInicioFiltro != null && dataTerminoFiltro == null){
            sql.append("and `rep_pedido`.`dat_recebido` = ? ")
            sqlParameters.put("dataInicioFiltro", dataInicioFiltro)
        }

        if (dataInicioFiltro != null && dataTerminoFiltro != null){
            sql.append("and `rep_pedido`.`dat_recebido` between ? and ?")
            sqlParameters.put("dataInicioFiltro", dataInicioFiltro)
            sqlParameters.put("dataTerminoFiltro", dataTerminoFiltro)
        }

        return jdbcTemplate.query(sql.toString(), sqlParameters.values.toTypedArray()){
            rs, _ -> PedidosRecebidosData(rs.getString("des_apelido"),
                rs.getString("des_estado"), rs.getInt("cod_representante"),
                rs.getDouble("vlr_pedido"), rs.getDate("dat_recebido"),
                rs.getString("situacao"), rs.getInt("num_pedido"),
                rs.getDate("dat_entrega1"), rs.getDate("dat_entrega2"),
                rs.getLong("num_documento"), rs.getString("nom_cliente"),
                rs.getString("des_cidade"), rs.getInt("num_pedidovinculado"),
                rs.getInt("qtd_pedido"), rs.getString("des_periodo"),
                rs.getString("pertence_regiao").orEmpty().equals("Sim"),
                rs.getString("rep_regiao"),
                rs.getString("marcas"),
                rs.getString("pedido_integrado").orEmpty().equals("Sim"))
        }
    }
}