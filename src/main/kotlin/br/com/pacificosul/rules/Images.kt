package br.com.pacificosul.rules

import br.com.pacificosul.data.Image
import javax.imageio.ImageIO
import javax.imageio.IIOImage
import java.awt.Color
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import java.awt.image.BufferedImage
import java.io.*
import java.util.Base64

object Images {

    fun toBase64(imageBytes: ByteArray): String? {
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    @Throws(IOException::class)
    fun resizeImageAsByteArray(widthHeight: Int?, inputStream: FileInputStream, ext: String): ByteArray {
        val imgOriginalImage = ImageIO.read(inputStream)
        val originalHeight = imgOriginalImage.height
        val originalWidht = imgOriginalImage.width

        var newHeight = 0
        var newWidth = 0

        var x = 0
        var y = 0

        if (originalHeight > originalWidht) {
            newHeight = widthHeight!!
            newWidth = computeNewWidth(originalWidht, originalHeight, widthHeight)
            x = (widthHeight - newWidth) / 2
        } else {
            newWidth = widthHeight!!
            newHeight = computeNewHeight(originalWidht, originalHeight, widthHeight)
            y = (widthHeight - newHeight) / 2
        }

        val img = imgOriginalImage.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH)
        val resizedImage = BufferedImage(widthHeight, widthHeight, BufferedImage.TYPE_INT_RGB)

        val iter = ImageIO.getImageWritersByFormatName("jpeg")
        val writer = iter.next() as ImageWriter
        val iwp = writer.defaultWriteParam
        iwp.compressionMode = ImageWriteParam.MODE_EXPLICIT
        iwp.compressionQuality = 0.3f


        val g2d = resizedImage.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, widthHeight, widthHeight)
        g2d.drawImage(img, x, y, null)
        g2d.dispose()

        val buffer = ByteArrayOutputStream()

        val image = IIOImage(resizedImage, null, null)
        writer.output = ImageIO.createImageOutputStream(buffer)
        writer.write(null, image, iwp)

        return buffer.toByteArray()
    }

    fun computeNewWidth(currentWidth: Int, currentHeight: Int, newWidth: Int): Int {
        return newWidth * currentWidth / currentHeight
    }

    fun computeNewHeight(currentWidth: Int, currentHeight: Int, newHeight: Int): Int {
        return newHeight * currentHeight / currentWidth
    }

    fun getName(path: String): String? {
        val regex = Regex("\\/(\\w*).jpg")
        return regex.find(path)?.groups?.get(1)?.value
    }

    fun getType(referencia: String, imagePath: String): String? {
        val regex = Regex(referencia + "_([a-zA-Z]*)_")
        return regex.find(imagePath)?.groups?.get(1)?.value
    }

    fun getSequence(imagePath: String): String? {
        val regex = Regex("_((?!_).\\w).jpg")
        return regex.find(imagePath)?.groups?.get(1)?.value
    }

    fun getImages(base: String, referencia: String) : HashMap<String, List<Image>> {
        val refPath = base + referencia + "/"
        val paths = arrayListOf<Image>()
        File(refPath).walkTopDown().forEach({
            x ->
                if(x.toString().endsWith(".jpg")) {
                    val image = createImage(referencia, x.toString())
                    paths.add(image)
                }
        })

        return hashMapOf(Pair(referencia, paths))
    }

    fun createImage(referencia: String, imagePath: String) : Image {
        val name = getName(imagePath)
        val type = getType(referencia, imagePath)
        val sequence = getSequence(imagePath)
        return Image(name, imagePath, type, sequence)
    }
}