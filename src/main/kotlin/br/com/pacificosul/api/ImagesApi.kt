package br.com.pacificosul.api

import br.com.pacificosul.rules.Images
import br.com.pacificosul.data.Image
import org.springframework.http.MediaType
import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMapping
import java.nio.file.Path
import java.nio.file.Paths
import java.io.IOException
import org.springframework.web.bind.annotation.GetMapping



@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/images")
class ImagesApi {

    val DIR_FOTOS_PRODUTOS = "/mnt/servidor/fotos/"
    val DIR_FOTOS_INSUMOS = "/mnt/servidor/produc/fotos/insumos/"

//    @GetMapping("produto")
//    @ResponseBody
//    fun get(@RequestParam("nivel") nivel: String,
//            @RequestParam("grupo") grupo: String,
//            @RequestParam("subGrupo") subGrupo: String,
//            @RequestParam("item") item: String): Image? {
//        val imagePath = findPath(nivel, grupo, subGrupo, item)
//        val name = Images.pathDestructor(imagePath)
//        return Image(name.orEmpty(), imagePath)
//    }
//
    @GetMapping("/produto/referencia/{referencia}")
    @ResponseBody
    fun get(@PathVariable("referencia") referencia: String): HashMap<String, List<Image>> {
        var ref = referencia
        if (referencia.first().isLetter()) {
            ref = referencia.replaceRange(0,1,"2")
        }
        return Images.getImages(DIR_FOTOS_PRODUTOS, ref)
    }

//    @GetMapping("/download/produto")
//    @ResponseBody
//    fun get(@RequestParam("nivel") nivel: String,
//            @RequestParam("grupo") grupo: String,
//            @RequestParam("subGrupo") subGrupo: String,
//            @RequestParam("item") item: String,
//            @RequestParam( "height", required = false) height: Int?): Image? {
//        val imagePath = findPath(nivel, grupo, subGrupo, item);
//        return getBase64(imagePath, height)
//    }

    @GetMapping("/base64/download")
    @ResponseBody
    fun get(@RequestParam("imagePath") imagePath: String,
            @RequestParam( "height", required = false) height: Int?): String {
        val file = File(imagePath)
        var bytes: ByteArray
        if (height != null) {
            bytes = Images.resizeImageAsByteArray(height, FileInputStream(file), "jpg")
        } else {
            bytes = file.readBytes()
        }

        val imageBase64 = Images.toBase64(bytes)

        return imageBase64.orEmpty()
    }

    @GetMapping(value = "/download", produces = arrayOf(MediaType.IMAGE_JPEG_VALUE))
    @ResponseBody
    @Throws(IOException::class)
    fun getImage(@RequestParam("imagePath") imagePath: String): ByteArray {
        val file = File(imagePath)
        return file.readBytes()
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

//    fun findPath(nivel: String, grupo: String, subGrupo: String, item: String): String {
//        return DIR_BASE + "Screenshot_20180102_105952.jpeg"
//    }
}