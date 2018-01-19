package br.com.pacificosul.rules

import javax.imageio.ImageIO
import javax.imageio.IIOImage
import java.awt.Color
import java.awt.Graphics2D
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import java.awt.image.BufferedImage
import java.io.*
import java.util.Base64;

object Images {

    fun toBase64(imageBytes: ByteArray): String? {
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    fun toImage(base64Image: String, pathFile: String) {
        try {
            FileOutputStream(pathFile).use { imageOutFile ->
                // Converting a Base64 String into Image byte array
                val imageByteArray = Base64.getDecoder().decode(base64Image)
                imageOutFile.write(imageByteArray)
            }
        } catch (e: FileNotFoundException) {
            println("Image not found" + e)
        } catch (ioe: IOException) {
            println("Exception while reading the Image " + ioe)
        }
    }

    @Throws(IOException::class)
    fun resizeImageAsByteArray(widthHeight: Int?, inputStream: InputStream, ext: String): ByteArray {
        val imgOriginalImage = ImageIO.read(inputStream)
        val originalHeight = imgOriginalImage.getHeight()
        val originalWidht = imgOriginalImage.getWidth()

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

}