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

        Assert.assertEquals("Assign[1] [b\$0, 1]\n" +
                "Allocate b\$0: Int\n" +
                "VarBound [b\$0]\n" +
                "Assign[3] [_\$0, 0.26]\n" +
                "Allocate _\$0: Real\n" +
                "Add[3] [0.1, _\$0, a\$0]\n" +
                "Allocate a\$0: Real\n" +
                "VarBound [a\$0]", output)
    }

    @Test
    fun testRealConstantInBlockToComplex() {
        withSource("var a: Cplx = { var b = 1; 0.26};")
        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [b\$0, 1]\n" +
                "Allocate b\$0: Int\n" +
                "VarBound [b\$0]\n" +
                "Assign[3] [_\$0, 0.26]\n" +
                "Allocate _\$0: Real\n" +
                "Cons[2] [_\$0, 0.0, a\$0]\n" +
                "Allocate a\$0: Cplx\n" +
                "VarBound [a\$0]", output)
    }

    @Test
    fun testSwitchExpr() {
        withSource("var a = 0; var b = [a + 1, a + 2, a + 3][a]")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [a\$0, 0]\n" +
                "Allocate a\$0: Int\n" +
                "Switch[0] [a\$0, 3, Label case\$0, Label case\$1, Label case\$2]\n" +
                "Label case\$0\n" +
                "Add[1] [1, a\$0, b\$0]\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label case\$1\n" +
                "Add[1] [2, a\$0, b\$0]\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label case\$2\n" +
                "Add[1] [3, a\$0, b\$0]\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label endSwitch\$0\n" +
                "Allocate b\$0: Int\n" +
                "VarBound [a\$0, b\$0]", output)
    }

    @Test
    fun testSwitchBool() {
        withSource("var a = 0; var c: Int; if([a == 0, a == 1][a]) { c = 2 } else { c = 3 }; c = c + 4")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [a\$0, 0]\n" +
                "Allocate a\$0: Int\n" +
                "Allocate c\$0: Int\n" +
                "Switch[0] [a\$0, 2, Label case\$0, Label case\$1]\n" +
                "Label case\$0\n" +
                "Equal[1] [0, a\$0, Label ifElseTrue\$0, Label ifElseFalse\$0]\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label case\$1\n" +
                "Equal[1] [1, a\$0, Label ifElseTrue\$0, Label ifElseFalse\$0]\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label endSwitch\$0\n" +
                "Label ifElseTrue\$0\n" +
                "Assign[1] [c\$0, 2]\n" +
                "VarBound []\n" +
                "Jump[0] [Label endIfElse\$0]\n" +
                "Label ifElseFalse\$0\n" +
                "Assign[1] [c\$0, 3]\n" +
                "VarBound []\n" +
                "Label endIfElse\$0\n" +
                "Add[1] [4, c\$0, c\$0]\n" +
                "VarBound [a\$0, c\$0]", output)
    }


    @Test
    fun testSwitchStmt() {
        withSource("var a = 0; var b: Int; [{b = 1}, {b = 2}][a]")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [a\$0, 0]\n" +
                "Allocate a\$0: Int\n" +
                "Allocate b\$0: Int\n" +
                "Switch[0] [a\$0, 2, Label case\$0, Label case\$1]\n" +
                "Label case\$0\n" +
                "Assign[1] [b\$0, 1]\n" +
                "VarBound []\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label case\$1\n" +
                "Assign[1] [b\$0, 2]\n" +
                "VarBound []\n" +
                "Jump[0] [Label endSwitch\$0]\n" +
                "Label endSwitch\$0\n" +
                "VarBound [a\$0, b\$0]", output)
    }

    @Test
    fun testConsts() {
        withSource("var a = pi + e + i;")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[5] [a\$0, 5.859874482048838:1.0]\n" +
                "Allocate a\$0: Cplx\n" +
                "VarBound [a\$0]", output)
    }

    @Test
    fun avoidSameAssignment() {
        withSource("var b = 1; b = b")

        actParse()

        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals("Assign[1] [b\$0, 1]\n" +
                "Allocate b\$0: Int\n" +
                "VarBound [b\$0]", output)
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

        Assert.assertEquals("Assign[1] [a\$0, 1]\n" +
                "Allocate a\$0: Int\n" +
                "VarBound [a\$0]", output)
    }

    @Test
    fun testAddition() {
        withSource("var a = 1;var b=2; var c=a + b + 3")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Assign[1] [b\$0, 2]\n" +
                    "Allocate b\$0: Int\n" +
                    "Add[1] [3, a\$0, _\$0]\n" +
                    "Allocate _\$0: Int\n" +
                    "Add[0] [_\$0, b\$0, c\$0]\n" +
                    "Allocate c\$0: Int\n" +
                    "VarBound [a\$0, b\$0, c\$0]", output)
    }

    @Test
    fun testBlock() {
        withSource("var a = 1 + {var b = 2; b + 3 }")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [b\$0, 2]\n" +
                    "Allocate b\$0: Int\n" +
                    "VarBound [b\$0]\n" +
                    "Add[1] [3, b\$0, _\$0]\n" +
                    "Allocate _\$0: Int\n" +
                    "Add[1] [1, _\$0, a\$0]\n" +
                    "Allocate a\$0: Int\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testIfStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Equal[1] [1, a\$0, Label ifTrue\$0, Label ifFalse\$0]\n" +
                    "Label ifTrue\$0\n" +
                    "Add[1] [1, a\$0, a\$0]\n" +
                    "Label ifFalse\$0\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testSetResult() {
        withSource("setResult(0, 1 / point, re(point));")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [_\$0]\n" +
                    "Allocate _\$0: Cplx\n" +
                    "Recip[1] [_\$0, _\$1]\n" +
                    "Allocate _\$1: Cplx\n" +
                    "Point[0] [_\$2]\n" +
                    "Allocate _\$2: Cplx\n" +
                    "RealPart[0] [_\$2, _\$3]\n" +
                    "Allocate _\$3: Real\n" +
                    "SetResult[1] [0, _\$1, _\$3]\n" +
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
            "Assign[5] [z\$0, 0.0]\n" +
                    "Allocate z\$0: Cplx\n" +
                    "Exp[1] [z\$0, a\$0]\n" +
                    "Allocate a\$0: Cplx\n" +
                    "VarBound [z\$0, a\$0]", output)
    }

    @Test
    fun testIfElseStmt() {
        withSource("var a = 1; if(a == 1) a = a + 1 else a = a + 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Equal[1] [1, a\$0, Label ifElseTrue\$0, Label ifElseFalse\$0]\n" +
                    "Label ifElseTrue\$0\n" +
                    "Add[1] [1, a\$0, a\$0]\n" +
                    "Jump[0] [Label endIfElse\$0]\n" +
                    "Label ifElseFalse\$0\n" +
                    "Add[1] [2, a\$0, a\$0]\n" +
                    "Label endIfElse\$0\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testIfElseExpr() {
        withSource("var a = 1; a = if(a == 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Equal[1] [1, a\$0, Label ifElseTrue\$0, Label ifElseFalse\$0]\n" +
                    "Label ifElseTrue\$0\n" +
                    "Assign[1] [a\$0, 1]\n" +
                    "Jump[0] [Label ifElseEnd\$0]\n" +
                    "Label ifElseFalse\$0\n" +
                    "Assign[1] [a\$0, 2]\n" +
                    "Label ifElseEnd\$0\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testIfElseLessExpr() {
        withSource("var a = 1; a = if(a < 1) 1 else 2")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Less[2] [a\$0, 1, Label ifElseTrue\$0, Label ifElseFalse\$0]\n" +
                    "Label ifElseTrue\$0\n" +
                    "Assign[1] [a\$0, 1]\n" +
                    "Jump[0] [Label ifElseEnd\$0]\n" +
                    "Label ifElseFalse\$0\n" +
                    "Assign[1] [a\$0, 2]\n" +
                    "Label ifElseEnd\$0\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testModExpr() {
        withSource("var a = 1; var b = a % 2; var c = 2 % a;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Mod[2] [a\$0, 2, b\$0]\n" +
                    "Allocate b\$0: Int\n" +
                    "Mod[1] [2, a\$0, c\$0]\n" +
                    "Allocate c\$0: Int\n" +
                    "VarBound [a\$0, b\$0, c\$0]", output)
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
            "Point[0] [c\$0]\n" +
                    "Allocate c\$0: Cplx\n" +
                    "RealPart[0] [c\$0, _\$0]\n" +
                    "Allocate _\$0: Real\n" +
                    "Cons[2] [_\$0, 0.0, c\$0]\n" +
                    "VarBound [c\$0]", output)
    }

    @Test
    fun testInlineMulWithId() {
        withSource("var c = point; c = c c;\n")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [c\$0]\n" +
                    "Allocate c\$0: Cplx\n" +
                    "Mul[4] [c\$0, c\$0, c\$0]\n" +
                    "VarBound [c\$0]", output)
    }

    @Test
    fun testVectorWithDifferentTypes() {
        withSource("var c: Cplx = point; var n = 0; var z = [c, 1][n];\n")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [c\$0]\n" +
                    "Allocate c\$0: Cplx\n" +
                    "Assign[1] [n\$0, 0]\n" +
                    "Allocate n\$0: Int\n" +
                    "Switch[0] [n\$0, 2, Label case\$0, Label case\$1]\n" +
                    "Label case\$0\n" +
                    "Assign[4] [z\$0, c\$0]\n" +
                    "Jump[0] [Label endSwitch\$0]\n" +
                    "Label case\$1\n" +
                    "Assign[5] [z\$0, 1.0]\n" +
                    "Jump[0] [Label endSwitch\$0]\n" +
                    "Label endSwitch\$0\n" +
                    "Allocate z\$0: Cplx\n" +
                    "VarBound [c\$0, n\$0, z\$0]", output)
    }

    @Test
    fun testDivExpr() {
        withSource("var a = 1; var b = a / 2; var c = 2 / a;")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "ToReal[0] [a\$0, _\$0]\n" +
                    "Allocate _\$0: Real\n" +
                    "Mul[3] [0.5, _\$0, b\$0]\n" +
                    "Allocate b\$0: Real\n" +
                    "ToReal[0] [a\$0, _\$1]\n" +
                    "Allocate _\$1: Real\n" +
                    "Div[1] [2.0, _\$1, c\$0]\n" +
                    "Allocate c\$0: Real\n" +
                    "VarBound [a\$0, b\$0, c\$0]", output)
    }

    @Test
    fun testWhileStmt() {
        withSource("var a = 1; while(a < 10) a = a + 1")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Assign[1] [a\$0, 1]\n" +
                    "Allocate a\$0: Int\n" +
                    "Label while\$0\n" +
                    "Less[2] [a\$0, 10, Label whileTrue\$0, Label endWhile\$0]\n" +
                    "Label whileTrue\$0\n" +
                    "Add[1] [1, a\$0, a\$0]\n" +
                    "Jump[0] [Label while\$0]\n" +
                    "Label endWhile\$0\n" +
                    "VarBound [a\$0]", output)
    }

    @Test
    fun testPointSetResult() {
        withSource("var a = point; setResult(1, a, 2)")

        actParse()
        actInline()
        actLinearize()

        actPrint()

        Assert.assertEquals(
            "Point[0] [a\$0]\n" +
                    "Allocate a\$0: Cplx\n" +
                    "SetResult[5] [1, a\$0, 2.0]\n" +
                    "VarBound [a\$0]", output)
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
        val varNameGenerator = NameGenerator()
        inlined.accept(LinearizeStmt(linearized, varNameGenerator))
    }

    private fun actInline() {
        val rootTable = RootSymbolTable(FractlangProgram.namedInstructions, emptyMap())

        val varNameGenerator = NameGenerator()

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