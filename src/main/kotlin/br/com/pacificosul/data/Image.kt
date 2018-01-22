package br.com.pacificosul.data

class Image {
    var name: String? = null
    var data: String? = null
    var tipo: String? = null

    constructor() {}

    constructor(name: String, data: String) {
        this.name = name
        this.data = data
    }

    constructor(name: String, data: String, tipo: String) {
        this.name = name
        this.data = data
        this.tipo = tipo
    }
}