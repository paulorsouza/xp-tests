package br.com.pacificosul.controller

import br.com.pacificosul.rules.Images
import br.com.pacificosul.data.Image
import org.springframework.http.MediaType
import java.io.File
import java.io.FileInputStream
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import org.springframework.web.bind.annotation.GetMapping



@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/images")
class ImagesController {

    val DIR_FOTOS_PRODUTOS = "/mnt/servidor/fotos/"
    val DIR_FOTOS_INSUMOS = "/mnt/servidor/produc/fotos/insumos/"

    @GetMapping("produto")
    @ResponseBody
    fun get(@RequestParam("nivel") nivel: String,
            @RequestParam("grupo") grupo: String,
            @RequestParam("subGrupo") subGrupo: String,
            @RequestParam("item") item: String): Image? {
        val imagePath = DIR_FOTOS_INSUMOS + nivel + grupo + subGrupo + item
        val name = Images.getName(imagePath)
        return Image(name.orEmpty(), imagePath, "tag", "1")
    }
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

}