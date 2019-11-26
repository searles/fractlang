package at.searles.fractlang

import at.searles.fractlang.linear.LinearCode
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.InlineVisitor
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class LinearizationTest {

    @Test
    fun avoidSameAssignment() {
        withSource("var b = 1; b = b")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("[Assign[1] [\$1, 1], alloc \$1: Int]", output)
    }

    @Test
    fun testAssignWhileToVarFail() {
        withSource("var b = 1; var a = { while(b==2) {} }")

        actParse()

        try {
            actInline()
            actLinearize()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testSimpleVar() {
        withSource("var a = 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("[Assign[1] [\$1, 1], alloc \$1: Int]", output)
    }

    @Test
    fun testAddition() {
        withSource("var a = 1;var b=2; var c=a + b + 3")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "Assign[1] [\$2, 2], " +
                    "alloc \$2: Int, " +
                    "Add[1] [3, \$1, R1], " +
                    "alloc R1: Int, " +
                    "Add[0] [R1, \$2, \$3], " +
                    "alloc \$3: Int" +
                    "]", output)
    }

    @Test
    fun testBlock() {
        withSource("var a = 1 + {var b = 2; b + 3 }")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 2], " +
                    "alloc \$1: Int, " +
                    "Add[1] [3, \$1, R1], " +
                    "alloc R1: Int, " +
                    "Add[1] [1, R1, \$2], " +
                    "alloc \$2: Int" +
                    "]", output)
    }

    @Test
    fun testIfStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "Equal[2] [\$1, 1, @14, @21], " +
                    "@14, " +
                    "Add[1] [1, \$1, \$1], " +
                    "@21" +
                    "]", output)
    }

    @Test
    fun testIfElseStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1; else a = a + 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "Equal[2] [\$1, 1, @14, @21], " +
                    "@14, " +
                    "Add[1] [1, \$1, \$1], " +
                    "@21" +
                    "]", output)
    }

    @Test
    fun testIfElseExpr() {
        withSource("var a = 1; a = a + if(a == 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "Equal[2] [\$1, 1, @14, @22], " +
                    "@14, " +
                    "Assign[1] [R1, 1], " +
                    "Jump[0] [@28], " +
                    "@22, " +
                    "Assign[1] [R1, 2], " +
                    "@28, " +
                    "alloc R1: Int, " +
                    "Add[0] [\$1, R1, \$1]" +
                    "]", output)
    }

    @Test
    fun testWhileStmt() {
        withSource("var a = 1; while(a < 10) a = a + 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[" +
                    "Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "@6, " +
                    "Less[2] [\$1, 10, @14, @23], " +
                    "@14, " +
                    "Add[1] [1, \$1, \$1], " +
                    "Jump[0] [@6], " +
                    "@23" +
                    "]", output)
    }

    private lateinit var output: String
    private lateinit var linearized: LinearCode
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private fun actPrint() {
        output = linearized.code.toString()
    }

    private fun actLinearize() {
        linearized = LinearCode()
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "R$it" }.iterator()
        inlined.accept(LinearizeStmt(linearized, varNameGenerator))
    }

    private fun actInline() {
        val rootTable = object: SymbolTable {
            override fun get(id: String): Node? {
                return null
            }
        }

        val varNameGenerator = generateSequence(1) { it + 1 }.map { "\$$it" }.iterator()

        inlined = ast.accept(
            InlineVisitor(
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