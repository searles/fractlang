package at.searles.meelan

import org.junit.Test

class SemanticAnalysisTest {
    @Test
    fun test() {
        val string = "// how does this work?\n" +
                "var f = 2;\n" +
                "class A(var a: Int) {\n" +
                "var b = a * f + 1;\n" +
                "}\n" +
                "{\n" +
                "def f = 6;\n" +
                "var d = A(3);\n" +
                "var e = d.b;\n" +
                "}\n"
    }
}