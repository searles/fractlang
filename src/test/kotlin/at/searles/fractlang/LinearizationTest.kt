package at.searles.fractlang

import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class LinearizationTest {

    @Test
    fun testRealConstantInBlockWithAddition() {
        withSource("var a = 0.1 + { var b = 1; 0.26};")
        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 1]\n" +
                "Allocate \$1: Int\n" +
                "VarBound [\$1]\n" +
                "Assign[3] [R1, 0.26]\n" +
                "Allocate R1: Real\n" +
                "Add[3] [0.1, R1, \$2]\n" + // could try to optimize this...
                "Allocate \$2: Real\n" +
                "VarBound [\$2]", output)
    }

    @Test
    fun testRealConstantInBlockToComplex() {
        withSource("var a: Cplx = { var b = 1; 0.26};")
        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 1]\n" +
                "Allocate \$1: Int\n" +
                "VarBound [\$1]\n" +
                "Assign[3] [R1, 0.26]\n" +
                "Allocate R1: Real\n" +
                "Cons[2] [R1, 0.0, \$2]\n" +
                "Allocate \$2: Cplx\n" +
                "VarBound [\$2]", output)
    }

    @Test
    fun testSwitchExpr() {
        withSource("var a = 0; var b = [a + 1, a + 2, a + 3][a]")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 0]\n" +
                "Allocate \$1: Int\n" +
                "Switch[0] [\$1, 3, Label R1, Label R2, Label R3]\n" +
                "Label R1\n" +
                "Add[1] [1, \$1, \$2]\n" +
                "Jump[0] [Label R4]\n" +
                "Label R2\n" +
                "Add[1] [2, \$1, \$2]\n" +
                "Jump[0] [Label R4]\n" +
                "Label R3\n" +
                "Add[1] [3, \$1, \$2]\n" +
                "Jump[0] [Label R4]\n" +
                "Label R4\n" +
                "Allocate \$2: Int\n" +
                "VarBound [\$1, \$2]", output)
    }

    @Test
    fun testSwitchBool() {
        withSource("var a = 0; var c: Int; if([a == 0, a == 1][a]) { c = 2 } else { c = 3 }; c = c + 4")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 0]\n" +
                "Allocate \$1: Int\n" +
                "Allocate \$2: Int\n" +
                "Switch[0] [\$1, 2, Label R4, Label R5]\n" +
                "Label R4\n" +
                "Equal[1] [0, \$1, Label R1, Label R2]\n" +
                "Jump[0] [Label R6]\n" +
                "Label R5\n" +
                "Equal[1] [1, \$1, Label R1, Label R2]\n" +
                "Jump[0] [Label R6]\n" +
                "Label R6\n" +
                "Label R1\n" +
                "Assign[1] [\$2, 2]\n" +
                "VarBound []\n" +
                "Jump[0] [Label R3]\n" +
                "Label R2\n" +
                "Assign[1] [\$2, 3]\n" +
                "VarBound []\n" +
                "Label R3\n" +
                "Add[1] [4, \$2, \$2]\n" +
                "VarBound [\$1, \$2]", output)
    }


    @Test
    fun testSwitchStmt() {
        withSource("var a = 0; var b: Int; [{b = 1}, {b = 2}][a]")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 0]\n" +
                "Allocate \$1: Int\n" +
                "Allocate \$2: Int\n" +
                "Switch[0] [\$1, 2, Label R1, Label R2]\n" +
                "Label R1\n" +
                "Assign[1] [\$2, 1]\n" +
                "VarBound []\n" +
                "Jump[0] [Label R3]\n" +
                "Label R2\n" +
                "Assign[1] [\$2, 2]\n" +
                "VarBound []\n" +
                "Jump[0] [Label R3]\n" +
                "Label R3\n" +
                "VarBound [\$1, \$2]", output)
    }

    @Test
    fun testConsts() {
        withSource("var a = pi + e + i;")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[5] [\$1, 5.859874482048838:1.0]\n" +
                "Allocate \$1: Cplx\n" +
                "VarBound [\$1]", output)
    }

    @Test
    fun avoidSameAssignment() {
        withSource("var b = 1; b = b")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [\$1, 1]\n" +
                "Allocate \$1: Int\n" +
                "VarBound [\$1]", output)
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
                "Allocate \$1: Int\n" +
                "VarBound [\$1]", output)
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
                    "Allocate \$3: Int\n" +
                    "VarBound [\$1, \$2, \$3]", output)
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
                    "VarBound [\$1]\n" +
                    "Add[1] [3, \$1, R1]\n" +
                    "Allocate R1: Int\n" +
                    "Add[1] [1, R1, \$2]\n" +
                    "Allocate \$2: Int\n" +
                    "VarBound [\$2]", output)
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
                    "Label R2\n" +
                    "VarBound [\$1]", output)
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
                    "Recip[1] [R1, R2]\n" +
                    "Allocate R2: Cplx\n" +
                    "Point[0] [R3]\n" +
                    "Allocate R3: Cplx\n" +
                    "RealPart[0] [R3, R4]\n" +
                    "Allocate R4: Real\n" +
                    "SetResult[1] [0, R2, R4]\n" +
                    "VarBound []", output)
    }

    @Test
    fun testExpBug() {
        withSource("var z = 0:0; var a: Cplx = exp z;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[5] [\$1, 0.0]\n" +
                    "Allocate \$1: Cplx\n" +
                    "Exp[1] [\$1, \$2]\n" +
                    "Allocate \$2: Cplx\n" +
                    "VarBound [\$1, \$2]", output)
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
                    "Label R3\n" +
                    "VarBound [\$1]", output)
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
                    "Label R3\n" +
                    "VarBound [\$1]", output)
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
                    "Label R3\n" +
                    "VarBound [\$1]", output)
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
                    "Allocate \$3: Int\n" +
                    "VarBound [\$1, \$2, \$3]", output)
    }

    @Test
    fun testAssignRealToCplx() {
        withSource(
            "var c = point;\n" +
                    "c = re c;\n")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [\$1]\n" +
                    "Allocate \$1: Cplx\n" +
                    "RealPart[0] [\$1, R1]\n" +
                    "Allocate R1: Real\n" +
                    "Cons[2] [R1, 0.0, \$1]\n" +
                    "VarBound [\$1]", output)
    }

    @Test
    fun testInlineMulWithId() {
        withSource("var c = point; c = c c;\n")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [\$1]\n" +
                    "Allocate \$1: Cplx\n" +
                    "Mul[4] [\$1, \$1, \$1]\n" +
                    "VarBound [\$1]", output)
    }

    @Test
    fun testVectorWithDifferentTypes() {
        withSource("var c: Cplx = point; var n = 0; var z = [c, 1][n];\n")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [\$1]\n" +
                    "Allocate \$1: Cplx\n" +
                    "Assign[1] [\$2, 0]\n" +
                    "Allocate \$2: Int\n" +
                    "Switch[0] [\$2, 2, Label R1, Label R2]\n" +
                    "Label R1\n" +
                    "Assign[4] [\$3, \$1]\n" +
                    "Jump[0] [Label R3]\n" +
                    "Label R2\n" +
                    "Assign[5] [\$3, 1.0]\n" +
                    "Jump[0] [Label R3]\n" +
                    "Label R3\n" +
                    "Allocate \$3: Cplx\n" +
                    "VarBound [\$1, \$2, \$3]", output)
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
                    "Mul[3] [0.5, R1, \$2]\n" +
                    "Allocate \$2: Real\n" +
                    "ToReal[0] [\$1, R2]\n" +
                    "Allocate R2: Real\n" +
                    "Div[1] [2.0, R2, \$3]\n" +
                    "Allocate \$3: Real\n" +
                    "VarBound [\$1, \$2, \$3]", output)
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
                    "Label R3\n" +
                    "VarBound [\$1]", output)
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
                    "SetResult[5] [1, \$1, 2.0]\n" +
                    "VarBound [\$1]", output)
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
        val rootTable = RootSymbolTable(FractlangProgram.namedInstructions, emptyMap())

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