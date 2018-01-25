package br.com.pacificosul.rules

import kotlin.test.assertEquals
import org.junit.Test
import java.awt.Image

class ImagesTest {
    @Test fun destructorTest() {
        val path = "/mnt/servidor/fotos/10795/10795_modelo_c02_s1.jpg"
        assertEquals("10795_modelo_c02_s1", Images.getName(path))
        assertEquals("modelo", Images.getType("10795", path))
        assertEquals("s1", Images.getSequence(path))
    }

    @Test fun listPathsTest() {
        /*TODO create resource in project to tests*/
        val dir = "/mnt/servidor/fotos/"
        val paths = Images.getImages(dir, "10795")
        assertEquals(1, paths.size)
    }

    @Test fun doNothing() {
        println("A2345".replaceRange(0,1,"2"))
    }
}