package at.searles.fractlang

import at.searles.fractlang.parsing.FractlangFormatter
import at.searles.parsingtools.formatter.EditableStringBuilder
import org.junit.Assert
import org.junit.Test

class FormatCodeTest {
    private lateinit var output: String

    @Test
    fun test() {
        format("{var a = 1;}")

        Assert.assertEquals("{\n" +
                "    var a = 1;\n" +
                "}", output)

    }

    @Test
    fun testStability() {
        format("{var a = 1;}")
        format(output)
        format(output)
        format(output)

        Assert.assertEquals("{\n" +
                "    var a = 1;\n" +
                "}", output)

    }

    fun format(source: String) {
        val sb = StringBuilder(source)
        val editable = EditableStringBuilder(sb)
        FractlangFormatter.format(editable)
        output = sb.toString()
    }
}