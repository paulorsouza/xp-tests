package br.com.pacificosul.data

data class GridColumnsDefData (
    val id: Int,
    val key: String,
    val name: String,
    val position: Int,
    val type: String,
    val formatter: Int?,
    val hidden: Boolean,
    val sortable: Boolean,
    val filterable: Boolean,
    val resizable: Boolean,
    val fixed: Boolean,
    val summary: Int
){}