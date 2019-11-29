package at.searles.fractlang

import at.searles.fractlang.linear.LinearizedCode
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

        Assert.assertEquals(listOf(0), vmCode)
    }

    @Test
    fun testAddition() {
        withSource("var a = 50; var b = 25; var c = a + b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }

    @Test
    fun testAdditionWithConst() {
        withSource("var a = 50; a = a + 25;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }

    @Test
    fun testRealAddition() {
        withSource("var a = 50.0; var b = 25.0; var c = a + b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }

    @Test
    fun testIfElse() {
        withSource("var a = 1; var b = 2; var c = if(a>b) a else b;")

        actParse()
        actInline()
        actLinearize()
        actCreateVmCode()

        Assert.assertEquals(listOf(0), vmCode)
    }


    private lateinit var vmCode: List<Int>
    private lateinit var linearized: LinearizedCode
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    val instructions = listOf<BaseOp>(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less)

    private fun actCreateVmCode() {
        vmCode = VmCodeAssembler(linearized, instructions).vmCode
    }

    private fun actLinearize() {
        linearized = LinearizedCode()
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "R$it" }.iterator()
        inlined.accept(LinearizeStmt(linearized, varNameGenerator))
    }

    private fun actInline() {
        val rootTable = RootSymbolTable(emptyMap(), emptyMap())

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