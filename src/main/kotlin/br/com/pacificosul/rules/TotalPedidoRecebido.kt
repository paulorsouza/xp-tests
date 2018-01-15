package br.com.pacificosul.rules

import br.com.pacificosul.data.PedidosRecebidosData
import br.com.pacificosul.data.TotalPedidosRecebidosData
import br.com.pacificosul.repository.TotalPedidosRecebidosRepository
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Date

fun totalPedidosRecebidos(jdbcTemplate: JdbcTemplate, dataFiltro: Date, showPedidosSistema: Boolean,
                          isGrouped: Boolean): List<TotalPedidosRecebidosData> {
    return TotalPedidosRecebidosRepository(jdbcTemplate).get(dataFiltro, showPedidosSistema, isGrouped)
}

fun pedidosRecebidos(jdbcTemplate: JdbcTemplate, periodo: String?, codRepresentante: Int?,
                     dataInicioFiltro: Date?, dataTerminoFiltro: Date?): List<PedidosRecebidosData> {
    return TotalPedidosRecebidosRepository(jdbcTemplate).
            getPedidos(periodo, codRepresentante, dataInicioFiltro, dataTerminoFiltro)
}