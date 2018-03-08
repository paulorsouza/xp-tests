package br.com.pacificosul.repository

import br.com.pacificosul.data.GridColumnsDefData
import br.com.pacificosul.data.GridProfileData
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
        val sql = "select a.id, key, name, type, formatter_index, summary_index, filterable, a.id_grid_column, " +
                  "       locked, resizable, sortable, hidden, position, id_grid_perfil, b.id_grid " +
                  "from pacificosul.CONF_GRID_PERFIL_COLUMN a " +
                  "join pacificosul.CONF_GRID_COLUMN b on b.id = a.id_grid_column " +
                  "where a.id_grid_perfil = :idPerfil " +
                  "order by position "
        val mapa = HashMap<String, Any>()
        mapa["idPerfil"] = idPerfil

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> GridColumnsDefData(rs.getInt("id"), rs.getInt("id_grid"),
                rs.getInt("id_grid_perfil"), rs.getInt("id_grid_column"), rs.getString("key"),
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

    fun getProfiles(gridName: String, codUsuario: Int): List<GridProfileData> {
        val sql = "select a.id, a.nome from pacificosul.CONF_GRID_PERFIL a " +
            "join pacificosul.conf_grid b " +
            "  on b.id = a.id_grid " +
            " and b.nome = :gridName " +
            "where id_usuario_gerenciador = :codUsuario " +
            "   or publico = 1 "

        val mapa = HashMap<String, Any>()
        mapa["gridName"] = gridName
        mapa["codUsuario"] = codUsuario

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> GridProfileData(rs.getInt("id"), rs.getString("nome"))
        }
    }

    fun getProfileName(gridName: String, profileName: String): String {
        val sql = "select 1 from pacificosul.CONF_GRID_PERFIL a " +
                  "join pacificosul.conf_grid b " +
                  "  on b.id = a.id_grid " +
                  " and b.nome = :gridName " +
                  "where a.nome = :profileName "
        val mapa = HashMap<String, Any>()
        mapa["gridName"] = gridName
        mapa["profileName"] = profileName
        println(sql)
        val alreadyExists = jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getInt(1)
        }.firstOrNull() != null

        /*TODO make this recursive to get a name already copied*/
        if(!alreadyExists) {
            return profileName
        }
        return "copy - ${profileName}"
    }

    fun getGridId(gridName: String): Int {
        val sql = "select id from pacificosul.conf_grid " +
                  "where nome = :gridName "
        val mapa = HashMap<String, Any>()
        mapa["gridName"] = gridName

        return jdbcTemplate.query(sql, mapa) {
            rs, _ -> rs.getInt(1)
        }.first()
    }

    fun createProfile(profileName: String, gridId: Int, userId: Int): Int {
        val sql = "select pacificosul.id_conf_grid_perfil.nextval from dual"
        val newId = jdbcTemplate.query(sql) { rs, _ -> rs.getInt(1) }.first()
        val insert = "insert into pacificosul.conf_grid_perfil " +
            "(id, id_grid, id_usuario_gerenciador, nome) " +
            "values " +
            "(:id, :id_grid, :id_usuario, :profileName) "

        val mapa = HashMap<String, Any>()
        mapa["id"] = newId
        mapa["id_grid"] = gridId
        mapa["id_usuario"] = userId
        mapa["profileName"] = profileName
        jdbcTemplate.update(insert, mapa)
        return newId
    }

    fun createColumns(newProfileId: Int, columns: List<GridColumnsDefData>) {
        val insert = "insert into pacificosul.conf_grid_perfil_column " +
                "(id_grid_perfil, id_grid_column, position, summary_index, hidden, locked) " +
                "values " +
                "(:id_grid_perfil, :id_grid_column, :position, :summary_index, :hidden, :locked) "

        columns.forEach { column ->
            val mapa = HashMap<String, Any>()
            mapa["id_grid_perfil"] = newProfileId
            mapa["id_grid_column"] = column.id_grid_column
            mapa["position"] = column.position
            mapa["summary_index"] = column.summary_index
            mapa["hidden"] = if(column.hidden) 1 else 0
            mapa["locked"] = if(column.locked) 1 else 0
            jdbcTemplate.update(insert, mapa)
        }
    }
}
