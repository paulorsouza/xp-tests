package br.com.pacificosul.repository

import br.com.pacificosul.data.GridColumnsDefData
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class GridProfileRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun getCurrentProfile(gridName: String, codUser: Int): Int? {
        val sql = "select nvl((select id_grid_perfil from pacificosul.conf_grid_perfil_usuario a " +
                  "            join pacificosul.conf_grid b on a.id_grid = b.id and b.nome = :gridName " +
                  "            where a.id_usuario = :codUser), " +
                  "           (select a.id from pacificosul.conf_grid_perfil a " +
                  "            join pacificosul.conf_grid b on a.id_grid = b.id and b.nome = :gridName " +
                  "            and a.nome = 'default')) as id_perfil from dual"
        val mapa = HashMap<String, Any>()
        mapa["gridName"] = gridName
        mapa["codUser"] = codUser

        println(sql)
        println(gridName)
        println(codUser)

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getInt("id_perfil")
        }.firstOrNull()
    }

    fun getColumnsDef(idPerfil: Int): List<GridColumnsDefData>? {
        val sql = "select a.id, key, name, type, formatter_index, summary_index, filterable, " +
                  "       locked, resizable, sortable, hidden, position, id_grid_perfil " +
                  "from pacificosul.CONF_GRID_PERFIL_COLUMN a " +
                  "join pacificosul.CONF_GRID_COLUMN b on b.id = a.id_grid_column " +
                  "where a.id_grid_perfil = :idPerfil " +
                  "order by position "
        val mapa = HashMap<String, Any>()
        mapa["idPerfil"] = idPerfil

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> GridColumnsDefData(rs.getInt("id"),
                rs.getInt("id_grid_perfil"), rs.getString("key"),
                rs.getString("name"), rs.getInt("position"), rs.getString("type"),
                rs.getInt("formatter_index"), (rs.getInt("hidden") == 1),
                (rs.getInt("sortable") == 1), (rs.getInt("filterable") == 1),
                (rs.getInt("resizable") == 1), (rs.getInt("locked") == 1),
                rs.getInt("summary_index")
            )
        }
    }

    fun updateColumnsDef(data: GridColumnsDefData) {
        val update = "update pacificosul.CONF_GRID_PERFIL_COLUMN " +
                "set summary_index = :summary_index, " +
                "    locked = :locked, " +
                "    resizable = :resizable, " +
                "    sortable = :sortable, " +
                "    hidden = :hidden, " +
                "    position = :position " +
                "where id = :id "

        val mapa = HashMap<String, Any>()
        mapa["summary_index"] = data.summary_index
        mapa["locked"] = data.locked
        mapa["resizable"] = data.resizable
        mapa["sortable"] = data.sortable
        mapa["hidden"] = data.hidden
        mapa["position"] = data.position
        mapa["id"] = data.id

        jdbcTemplate.update(update, mapa)
    }

    fun getProfiles(gridName: String, codUsuario: Int): List<Pair<Int, String>> {
        val sql = "select id, nome from pacificosul.CONF_GRID_PERFIL a " +
            "join pacificosul.conf_grid b " +
            "  on b.id = a.id_grid " +
            " and b.nome = :gridName " +
            "where id_usuario_gerenciador = :codUsuario " +
            "   or publico = 1 "

        val mapa = HashMap<String, Any>()
        mapa["gridName"] = gridName
        mapa["codUsuario"] = codUsuario

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> Pair(rs.getInt("id"), rs.getString("codUsuario"))
        }
    }
}
