package br.com.pacificosul.data

class Image {
    var name: String? = null
    var path: String? = null
    var type: String? = null
    var sequence: String? = null

    constructor()

    constructor(name: String?, path: String?) {
        this.name = name
        this.path = path
    }

    constructor(name: String?, path: String?, type: String?) {
        this.name = name
        this.path = path
        this.type = type
    }

    constructor(name: String?, path: String?, type: String?, sequence: String?) {
        this.name = name
        this.path = path
        this.type = type
        this.sequence = sequence
    }
}