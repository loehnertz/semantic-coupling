package utility

import codes.jakob.semanticcoupling.utility.Utilities
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class UtilitiesTest {
    @Test
    fun isNonEmptyWordEntry() {
        assertEquals(Utilities.isNonEmptyWordEntry("word"), true)
        assertEquals(Utilities.isNonEmptyWordEntry(""), false)
        assertEquals(Utilities.isNonEmptyWordEntry(" "), false)
    }
}
