package br.com.pacificosul.api

import br.com.pacificosul.rules.Images
import br.com.pacificosul.data.Image
import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMapping
import java.nio.file.Path
import java.nio.file.Paths

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

    @GetMapping("/download/produtoConfeccionado/referencia/{referencia}")
    @ResponseBody
    fun get(@PathVariable("referencia") referencia: String,
            @RequestParam("tipo", required = false) tipo: String?,
            @RequestParam("item", required = false) item: String?,
            @RequestParam( "height", required = false) height: Int?,
            @RequestParam("subGrupo", required = false) subGrupo: String?): List<Image> {
        val paths: List<String> = listPaths(referencia);
        val images = arrayListOf<Image>()
        paths.forEach({ path ->
            println(path)
            images.add(getBase64(path, height))
        })
        return images
    }

    @GetMapping("/download/produto")
    @ResponseBody
    fun get(@RequestParam("nivel") nivel: String,
            @RequestParam("grupo") grupo: String,
            @RequestParam("subGrupo") subGrupo: String,
            @RequestParam("item") item: String,
            @RequestParam( "height", required = false) height: Int?): Image? {
        val imagePath = findPath(nivel, grupo, subGrupo, item);
        return getBase64(imagePath, height)
    }

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

    fun getBase64(path: String, height: Int?): Image {
        val file = File(path)
        var bytes: ByteArray
        if (height != null) {
            bytes = Images.resizeImageAsByteArray(height, FileInputStream(file), "jpg")
        } else {
            bytes = file.readBytes()
        }
        val base64 = Images.toBase64(bytes)
        return Image(file.nameWithoutExtension, base64.orEmpty())
    }

    fun findPath(nivel: String, grupo: String, subGrupo: String, item: String): String {
        return DIR_BASE + "Screenshot_20180102_105952.jpeg"
    }

    fun listPaths(referencia: String) : List<String> {
        val paths = arrayListOf<String>()
        val dir = Paths.get("/home/prs/Pictures/")
        File("/home/prs/Pictures/").walkTopDown().forEach({
            x -> if(x.isFile && !x.isHidden) paths.add(x.toString())
        })
        return paths;
    }
}