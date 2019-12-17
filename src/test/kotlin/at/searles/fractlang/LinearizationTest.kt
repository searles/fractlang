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

        Assert.assertEquals("Assign[1] [\$1, 1]\n" +
                "Allocate \$1: Int", output)
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

        Assert.assertEquals("Assign[1] [\$1, 1]\n" +
                "Allocate \$1: Int", output)
    }

    @Test
    fun testAddition() {
        withSource("var a = 1;var b=2; var c=a + b + 3")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Assign[1] [\$2, 2]\n" +
                    "Allocate \$2: Int\n" +
                    "Add[1] [3, \$1, R1]\n" +
                    "Allocate R1: Int\n" +
                    "Add[0] [R1, \$2, \$3]\n" +
                    "Allocate \$3: Int", output)
    }

    @Test
    fun testBlock() {
        withSource("var a = 1 + {var b = 2; b + 3 }")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 2]\n" +
                    "Allocate \$1: Int\n" +
                    "Add[1] [3, \$1, R1]\n" +
                    "Allocate R1: Int\n" +
                    "Add[1] [1, R1, \$2]\n" +
                    "Allocate \$2: Int", output)
    }

    @Test
    fun testIfStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Equal[1] [1, \$1, Label R1, Label R2]\n" +
                    "Label R1\n" +
                    "Add[1] [1, \$1, \$1]\n" +
                    "Label R2", output)
    }

    @Test
    fun testSetResult() {
        withSource("setResult(0, 1 / point, re(point));")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [R1]\n" +
                    "Allocate R1: Cplx\n" +
                    "Reciprocal[0] [R1, R2]\n" +
                    "Allocate R2: Cplx\n" +
                    "Point[0] [R3]\n" +
                    "Allocate R3: Cplx\n" +
                    "RealPart[0] [R3, R4]\n" +
                    "Allocate R4: Real\n" +
                    "SetResult[1] [0, R2, R4]", output)
    }

    @Test
    fun testIfElseStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1 else a = a + 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Equal[1] [1, \$1, Label R1, Label R2]\n" +
                    "Label R1\n" +
                    "Add[1] [1, \$1, \$1]\n" +
                    "Jump[0] [Label R3]\n" +
                    "Label R2\n" +
                    "Add[1] [2, \$1, \$1]\n" +
                    "Label R3", output)
    }

    @Test
    fun testIfElseExpr() {
        withSource("var a = 1; a = if(a == 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Equal[1] [1, \$1, Label R1, Label R2]\n" +
                    "Label R1\n" +
                    "Assign[1] [\$1, 1]\n" +
                    "Jump[0] [Label R3]\n" +
                    "Label R2\n" +
                    "Assign[1] [\$1, 2]\n" +
                    "Label R3", output)
    }

    @Test
    fun testIfElseLessExpr() {
        withSource("var a = 1; a = if(a < 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Less[2] [\$1, 1, Label R1, Label R2]\n" +
                    "Label R1\n" +
                    "Assign[1] [\$1, 1]\n" +
                    "Jump[0] [Label R3]\n" +
                    "Label R2\n" +
                    "Assign[1] [\$1, 2]\n" +
                    "Label R3", output)
    }

    @Test
    fun testModExpr() {
        withSource("var a = 1; var b = a % 2; var c = 2 % a;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Mod[2] [\$1, 2, \$2]\n" +
                    "Allocate \$2: Int\n" +
                    "Mod[1] [2, \$1, \$3]\n" +
                    "Allocate \$3: Int", output)
    }

    @Test
    fun testDivExpr() {
        withSource("var a = 1; var b = a / 2; var c = 2 / a;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "ToReal[0] [\$1, R1]\n" +
                    "Allocate R1: Real\n" +
                    "Mul[1] [0.5, R1, \$2]\n" +
                    "Allocate \$2: Real\n" +
                    "ToReal[0] [\$1, R2]\n" +
                    "Allocate R2: Real\n" +
                    "Div[1] [2.0, R2, \$3]\n" +
                    "Allocate \$3: Real", output)
    }

    @Test
    fun testWhileStmt() {
        withSource("var a = 1; while(a < 10) a = a + 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [\$1, 1]\n" +
                    "Allocate \$1: Int\n" +
                    "Label R1\n" +
                    "Less[2] [\$1, 10, Label R2, Label R3]\n" +
                    "Label R2\n" +
                    "Add[1] [1, \$1, \$1]\n" +
                    "Jump[0] [Label R1]\n" +
                    "Label R3", output)
    }

    @Test
    fun testPointSetResult() {
        withSource("var a = point; setResult(1, a, 2)")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [\$1]\n" +
                    "Allocate \$1: Cplx\n" +
                    "SetResult[5] [1, \$1, 2.0]", output)
    }

    private lateinit var output: String
    private lateinit var linearized: ArrayList<CodeLine>
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private fun actPrint() {
        output = linearized.joinToString("\n")
    }

    private fun actLinearize() {
        linearized = ArrayList()
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "R$it" }.iterator()
        inlined.accept(LinearizeStmt(linearized, varNameGenerator))
    }

    private fun actInline() {
        val rootTable = RootSymbolTable(CompilerInstance.namedInstructions, emptyMap())

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