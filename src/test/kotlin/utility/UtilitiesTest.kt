package utility

import codes.jakob.semanticcoupling.utility.Utilities
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals


internal class UtilitiesTest {
    @Test
    fun isNonEmptyWordEntry() {
        assertEquals(Utilities.isNonEmptyWordEntry("word"), true)
        assertEquals(Utilities.isNonEmptyWordEntry(""), false)
        assertEquals(Utilities.isNonEmptyWordEntry(" "), false)
    }
}
