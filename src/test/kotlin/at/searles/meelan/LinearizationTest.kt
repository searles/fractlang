package at.searles.meelan

import at.searles.meelan.nodes.Block
import at.searles.meelan.nodes.Node
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class LinearizationTest {

    @Test
    fun testSimpleVar() {
        withSource("var a = 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("\$1=1;var\$1:Int;", output)
    }

    @Test
    fun testAddition() {
        withSource("var a = 1;var b=2; var c=a + b + 3")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("\$1=1;var\$1:Int;" +
                "\$2=2;var\$2:Int;" +
                "R1=\$1+\$2;varR1:Int;" +
                "R2=R1+3;varR2:Int;" +
                "\$3=R2;var\$3:Int;", output)
    }

    @Test
    fun testBlock() {
        withSource("var a = 1 + {var b = 2; b +3 }")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "\$1=2;var\$1:Int;" +
                "R1=\$1+3;varR1:Int;" +
                "R2=1+R1;varR2:Int;" +
                "\$2=R2;var\$2:Int;", output)
    }

    private lateinit var output: String
    private lateinit var linearized: Block
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private fun actPrint() {
        output = Meelan.program.print(linearized).toString()
    }

    private fun actLinearize() {
        val stmts = ArrayList<Node>()
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "R$it" }.iterator()
        inlined.accept(LinearizeStmt(stmts, varNameGenerator))
        linearized = Block(inlined.trace, stmts)
    }

    private fun actInline() {
        val rootTable = object: SymbolTable {
            override fun get(id: String): Node? {
                return null
            }
        }

        val varNameGenerator = generateSequence(1) { it + 1 }.map { "\$$it" }.iterator()

        inlined = ast.accept(InlineVisitor(rootTable, varNameGenerator))
    }

    private fun actParse() {
        ast = Meelan.program.parse(stream)!!
    }

    private fun withSource(src: String) {
        stream = ParserStream.fromString(src)
    }
}