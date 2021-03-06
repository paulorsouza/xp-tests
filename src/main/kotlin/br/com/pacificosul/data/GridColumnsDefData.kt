package br.com.pacificosul.data

data class GridColumnsDefData (
    val id: Int,
    val id_grid: Int,
    val id_grid_perfil: Int,
    val id_grid_column: Int,
    val key: String,
    val name: String,
    val position: Int,
    val type: String,
    val formatter_index: Int?,
    val hidden: Boolean,
    val sortable: Boolean,
    val filterable: Boolean,
    val resizable: Boolean,
    val locked: Boolean,
    val summary_index: Int
){}