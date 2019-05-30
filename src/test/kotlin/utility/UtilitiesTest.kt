package utility

import codes.jakob.semanticcoupling.utility.Utilities
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals


internal class UtilitiesTest {
    private val fileName = "test.txt"
    private val fileContents = "just a test"

    @BeforeEach
    fun setUp() {
        val testFile = File("src/main/resources/$fileName")
        testFile.createNewFile()
        testFile.writeText(fileContents)
    }

    @AfterEach
    fun tearDown() {
        val testFile = File("src/main/resources/$fileName")
        testFile.delete()
    }

    @Test
    fun getResourceAsText() {
        assertEquals(Utilities.getResourceAsText(fileName), fileContents)
    }

    @Test
    fun isNonEmptyWordEntry() {
        assertEquals(Utilities.isNonEmptyWordEntry("word"), true)
        assertEquals(Utilities.isNonEmptyWordEntry(""), false)
        assertEquals(Utilities.isNonEmptyWordEntry(" "), false)
    }
}
