package br.com.pacificosul.controller

import br.com.pacificosul.data.Image
import br.com.pacificosul.rules.Images
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000", "http://192.168.0.193:3000", "http://localhost:8080"))
@RequestMapping("/api/images")
class ImagesController {

    val DIR_FOTOS_PRODUTOS = "/mnt/servidor/fotos/"
    val DIR_FOTOS_INSUMOS = "/mnt/servidor/produc/fotos/insumos/"

    @GetMapping("insumo/{insumo}")
    @ResponseBody
    fun getInsumo(@PathVariable("insumo") insumo: String): HashMap<String, List<Image>> {
        val imagePath = (DIR_FOTOS_INSUMOS + insumo + ".jpg").toLowerCase()
        val name = Images.getName(imagePath)
        val image =  Image(name.orEmpty(), imagePath, "tag", "1")
        val paths = arrayListOf<Image>()
        paths.add(image)
        return hashMapOf(Pair(insumo, paths))
    }

    @GetMapping("produto/referencia/{referencia}")
    @ResponseBody
    fun getProduto(@PathVariable("referencia") referencia: String): HashMap<String, List<Image>> {
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
    fun donwload(@RequestParam("imagePath") imagePath: String,
                 @RequestParam("height", required = false) height: Int?): ByteArray {
        val file = File(imagePath)
        var bytes: ByteArray
        if (height != null) {
            bytes = Images.resizeImageAsByteArray(height, FileInputStream(file), "jpg")
        } else {
            bytes = file.readBytes()
        }
        return bytes
    }
}