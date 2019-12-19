package at.searles.fractlang

import org.junit.Assert
import org.junit.Test

class VmCodeTest {
    @Test
    fun testSimpleAssignment() {
        withSource("var b = 99;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testMandelbrot() {
        withSource("val z0 = 0:0;\n" +
                "var c = point;\n" +
                "var n = 0;\n" +
                "\n" +
                "var z = z0;\n" +
                "\n" +
                "val bailoutValue = 64;\n" +
                "val maxExponent = 2;\n" +
                "val maxIterationCount = 1024;\n" +
                "\n" +
                "while ({\n" +
                "\tz = z^maxExponent + c;\n" +
                "\t\n" +
                "\tvar radZ = rad z;\n" +
                "\t\n" +
                "\tif(radZ > bailoutValue) {\n" +
                "\t\tvar continuousAddend = -log(radZ / log bailoutValue) / log maxExponent;\n" +
                "\t\tvar continuousN = n + continuousAddend;\n" +
                "\t\tsetResult(1, log (1 + continuousN), continuousN);\n" +
                "\t\tfalse\n" +
                "\t} else if(not next(maxIterationCount, n)) {\n" +
                "\t\tsetResult(0, arc z / 2 pi, log radZ);\n" +
                "\t\tfalse\n" +
                "\t} else {\n" +
                "\t\ttrue\n" +
                "\t}\n" +
                "})")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testIfBoolBlock() {
        withSource("var a = 1; if(if(a == 1) { a = a + 1; false } else { true }) a = a + 2;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testMandelbrotBug0() {
        withSource("var n = 0;\n" +
                "var c = point;\n" +
                "c = 2;\n" +
                "var z = 0:0;\n" +
                "\n" +
                "while ({\n" +
                "\tz = z^2 + c;\n" +
                "\tvar radZ = rad z;\n" +
                "\tif(radZ > 4) {\n" +
                "\t\tsetResult(0, n, 0);\n" +
                "\t\tfalse\n" +
                "\t} else if(not next(10, n)) {\n" +
                "\t\tsetResult(1, 0, 0);\n" +
                "\t\tfalse\n" +
                "\t} else {\n" +
                "\t\ttrue\n" +
                "\t}\n" +
                "})")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }


    @Test
    fun testMandelbrotBug1() {
        withSource("var n = 0;\n" +
                "var c = point;\n" +
                "var z = 0:0;\n" +
                "\n" +
                "while ({\n" +
                "\tz = z*z + c;\n" + // here is a problem, probably an overlap of variables.
                "\tvar radZ = rad z;\n" +
                "\tif(radZ > 4) {\n" +
                "\t\tsetResult(0, n * 0.1, 0);\n" +
                "\t\tfalse\n" +
                "\t} else if(not next(10, n)) {\n" +
                "\t\tsetResult(1, 0, 0);\n" +
                "\t\tfalse\n" +
                "\t} else {\n" +
                "\t\ttrue\n" +
                "\t}\n" +
                "})")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testMandelbrotBug2() {
        withSource("var n = 0;\n" +
                "var c = point;\n" +
                "var z = 0:0;\n" +
                "\n" +
                "while ({\n" +
                "\tz = z^2 + c;\n" +
                "\n" +
                "\tvar radZ = rad z;\n" +
                "\n" +
                "\tif(radZ > 4) {\n" +
                "\t\tvar continuousAddend = 1.0 - radZ;\n" +
                "\n" +
                "\t\tif(continuousAddend < 0) continuousAddend = 0;\n" +
                "\t\tif(continuousAddend > 1) continuousAddend = 1;\n" +
                "\n" +
                "\t\tsetResult(0, n / 9.9, 0);\n" +
                "\t\tfalse\n" +
                "\t} else if(not next(10, n)) {\n" +
                "\t\tsetResult(1, 0, 0);\n" +
                "\t\tfalse\n" +
                "\t} else {\n" +
                "\t\ttrue\n" +
                "\t}\n" +
                "})")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }


    @Test
    fun testSimpleMultiplicationError() {
        withSource("var a = 5 point;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testAddition() {
        withSource("var a = 50; var b = 25; var c = a + b;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 50, 30, 1, 25, 0, 0, 1, 0), vmCode)
    }

    @Test
    fun testAdditionWithConst() {
        withSource("var a = 50; a = a + 25;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 50, 1, 25, 0, 0), vmCode)
    }

    @Test
    fun testRealAddition() {
        withSource("var a = 50.0; var b = 25.0; var c = a + b;")

        actCreateVmCode()

        Assert.assertEquals(listOf(32, 0, 0, 1078525952, 32, 2, 0, 1077477376, 2, 0, 2, 0), vmCode)
    }

    @Test
    fun testIfElseAndSelfAssignmentRemoval() {
        withSource("var a = 1; var b = 2; var c = if(a>b) a else b;")

        actCreateVmCode()

        Assert.assertEquals(listOf(30, 1, 1, 30, 0, 2, 38, 0, 1, 11, 16, 29, 0, 1, 35, 16), vmCode)
    }

    @Test
    fun testPointAndSetResult() {
        withSource("setResult(0, 0.5, 0);")

        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }

    @Test
    fun testCons() {
        withSource("setResult(0, sin rad point, 0);")

        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }

    private lateinit var ci: CompilerInstance
    private lateinit var source: String
    private lateinit var vmCode: List<Int>

    private fun actCreateVmCode() {
        ci.compile()
        vmCode = ci.vmCode
    }

    private fun withSource(src: String) {
        source = src
        ci = CompilerInstance(src,  emptyMap())
    }
}