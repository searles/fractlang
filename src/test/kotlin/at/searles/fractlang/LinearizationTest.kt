package at.searles.fractlang

import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.ops.Point
import at.searles.fractlang.ops.SetResult
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
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
            "[Assign[1] [\$1, 1], alloc \$1: Int, Equal[2] [\$1, 1, @R1, @R2], @R1, Add[1] [1, \$1, \$1], @R2]", output)
    }

    @Test
    fun testIfElseStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1; else a = a + 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[Assign[1] [\$1, 1], alloc \$1: Int, Equal[2] [\$1, 1, @R1, @R2], @R1, Add[1] [1, \$1, \$1], @R2]", output)
    }

    @Test
    fun testIfElseExpr() {
        withSource("var a = 1; a = a + if(a == 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[Assign[1] [\$1, 1], " +
                    "alloc \$1: Int, " +
                    "Equal[2] [\$1, 1, @R2, @R3], " +
                    "@R2, " +
                    "Assign[1] [R1, 1], " +
                    "Jump[0] [@R4], " +
                    "@R3, " +
                    "Assign[1] [R1, 2], " +
                    "@R4, " +
                    "alloc R1: Int, " +
                    "Add[0] [\$1, R1, \$1]]", output)
    }

    @Test
    fun testWhileStmt() {
        withSource("var a = 1; while(a < 10) a = a + 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[Assign[1] [\$1, 1], alloc \$1: Int, @R1, Less[2] [\$1, 10, @R2, @R3], @R2, Add[1] [1, \$1, \$1], Jump[0] [@R1], @R3]", output)
    }

    @Test
    fun testPointSetResult() {
        withSource("var a = point; setResult(1, a, 2)")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "[Point[0] [\$1], alloc \$1: Cplx, SetResult[5] [1, \$1, 2.0]]", output)
    }

    private lateinit var output: String
    private lateinit var linearized: ArrayList<CodeLine>
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private fun actPrint() {
        output = linearized.toString()
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