package br.com.pacificosul.api

import br.com.pacificosul.rules.Images
import br.com.pacificosul.data.Image
import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/images")
class ImagesApi {

    val DIR_BASE = "/home/prs/Pictures/"

    @PostMapping("/upload")
    fun post(@RequestBody image: Image): String {
        val path = DIR_BASE + image.name
        Images.toImage(image.data.orEmpty(), path)
        return "WIP - !"
    }

//    @GetMapping("/download/produtoConfeccionado/referencia/{referencia}")
//    @ResponseBody
//    fun get(@RequestParam("subGrupo", required = false) subGrupo: String?): List<Image> {
//
//    }
//
//    @GetMapping("/download/produto")
//    @ResponseBody
//    fun get(@RequestParam("nivel") nivel: String,
//            @RequestParam("grupo") grupo: String,
//            @RequestParam("subGrupo") subGrupo: String,
//            @RequestParam("item") item: String,
//            @RequestParam( "height", required = false) height: Int?): Image? {
//        val imagePath = searchPath(nivel, grupo, subGrupo, item);
//        val file = File(imagePath)
//        var bytes: ByteArray
//        if (height != null) {
//            bytes = Images.resizeImageAsByteArray(height, FileInputStream(file), "jpg")
//        } else {
//            bytes = file.readBytes()
//        }
//
//        val imageBase64 = Images.toBase64(bytes)
//
//        return if (imageBase64 != null) {
//            Image("teste", imageBase64)
//        } else null
//    }

    @GetMapping("/downloadByFileName")
    @ResponseBody
    fun get(@RequestParam("fileName") fileName: String,
            @RequestParam( "height", required = false) height: Int?): Image? {
        val imagePath = DIR_BASE + fileName
        val file = File(imagePath)
        var bytes: ByteArray
        if (height != null) {
            bytes = Images.resizeImageAsByteArray(height, FileInputStream(file), "jpg")
        } else {
            bytes = file.readBytes()
        }

        val imageBase64 = Images.toBase64(bytes)

        return if (imageBase64 != null) {
            Image(fileName, imageBase64)
        } else null
    }

}