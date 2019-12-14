package at.searles.fractlang

import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.vm.VmCodeAssembler
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.ops.*
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class VmCodeTest {
    @Test
    fun testSimpleAssignment() {
        withSource("var b = 99;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 99), vmCode)
    }

    @Test
    fun testAddition() {
        withSource("var a = 50; var b = 25; var c = a + b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 50, 30, 1, 25, 0, 0, 1, 0), vmCode)
    }

    @Test
    fun testAdditionWithConst() {
        withSource("var a = 50; a = a + 25;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(30, 0, 50, 1, 25, 0, 0), vmCode)
    }

    @Test
    fun testRealAddition() {
        withSource("var a = 50.0; var b = 25.0; var c = a + b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(32, 0, 0, 1078525952, 32, 2, 0, 1077477376, 2, 0, 2, 0), vmCode)
    }

    @Test
    fun testIfElseAndSelfAssignmentRemoval() {
        withSource("var a = 1; var b = 2; var c = if(a>b) a else b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(30, 1, 1, 30, 0, 2, 38, 0, 1, 11, 16, 29, 0, 1, 35, 16), vmCode)
    }

    @Test
    fun testPointAndSetResult() {
        withSource("var a = point(); setResult(1, 2 : 3, 4);")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(42, 0, 50, 1, 0, 1073741824, 0, 1074266112, 0, 1074790400), vmCode)
    }

    private lateinit var vmCode: List<Int>
    private lateinit var linearized: ArrayList<CodeLine>
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private val instructions = listOf<BaseOp>(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less, Point, SetResult)

    private fun actCreateVmCode() {
        vmCode = VmCodeAssembler(linearized, instructions).vmCode
    }

    private fun actLinearize() {
        linearized = ArrayList()
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "R$it" }.iterator()
        inlined.accept(LinearizeStmt(linearized, varNameGenerator))
    }

    private fun actInline() {
        val rootTable = RootSymbolTable(mapOf("point" to Point, "setResult" to SetResult), emptyMap())

        val varNameGenerator = generateSequence(1) { it + 1 }.map { "\$$it" }.iterator()

        inlined = ast.accept(
            SemanticAnalysisVisitor(
                rootTable,
                varNameGenerator
            )
        )
    }

    private fun actParse() {
        ast = FractlangParser.program.parse(stream)!!
    }

    private fun withSource(src: String) {
        stream = ParserStream.fromString(src)
    }
}