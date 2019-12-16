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
        withSource("setResult(0, 1 / point, re(point * point));")

        actCreateVmCode()

        Assert.assertEquals(listOf(48, 0, 29, 0, 0, 48, 4, 46, 4, 4, 48, 6, 46, 6, 6, 14, 4, 6, 4, 50, 0, 0, 4), vmCode)
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