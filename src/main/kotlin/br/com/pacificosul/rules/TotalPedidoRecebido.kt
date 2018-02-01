package br.com.pacificosul.rules

import br.com.pacificosul.data.PedidosRecebidosData
import br.com.pacificosul.data.TotalPedidosRecebidosData
import br.com.pacificosul.repository.TotalPedidosRecebidosRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Date

fun totalPedidosRecebidos(jdbcTemplate: NamedParameterJdbcTemplate, dataFiltro: Date, showPedidosSistema: Boolean,
                          isGrouped: Boolean): List<TotalPedidosRecebidosData> {
    return TotalPedidosRecebidosRepository(jdbcTemplate).get(dataFiltro, showPedidosSistema, isGrouped)
}

fun pedidosRecebidos(jdbcTemplate: NamedParameterJdbcTemplate, periodo: String?, codRepresentante: Int?,
                     dataInicioFiltro: Date?, dataTerminoFiltro: Date?): List<PedidosRecebidosData> {
    return TotalPedidosRecebidosRepository(jdbcTemplate).
            getPedidos(periodo, codRepresentante, dataInicioFiltro, dataTerminoFiltro)
}