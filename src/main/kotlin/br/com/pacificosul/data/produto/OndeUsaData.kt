package br.com.pacificosul.data.produto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class OndeUsaData {
    var nivelItem: String? = null
    var grupoItem: String? = null
    var subItem: String? = null
    var itemItem: String? = null
    var nivelComp: String? = null
    var grupoComp: String? = null
    var subComp: String? = null
    var itemComp: String? = null
    var unidadeMedida: String? = null
    var ano: String? = null
    var indiceEstacao: String? = null
    var descrColecao: String? = null
    var referencia: String? = null
    var alternativaItem: Int? = null
    var alternativaComp: Int? = null
    var consumo: Int? = null
    var pecasPrevistas: Int? = null

}
