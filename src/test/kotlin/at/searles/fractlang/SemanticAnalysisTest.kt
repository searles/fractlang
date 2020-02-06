package at.searles.fractlang

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class SemanticAnalysisTest {

    @Test
    fun testAssignWhileToVarFail() {
        withSource("var b = 1; var a = { while(b==2) {} }")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testVectorAccessConversion() {
        withSource("var a = [1, 2.0][0]; ")
        actParse()
        actInline()
        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;", output)
    }

    @Test
    fun testVectorAccessNegativeIndex() {
        withSource("var a = [1, 2, 3][-1]; ")
        actParse()
        actInline()
        actPrint()

        Assert.assertEquals("_1=3;var_1:Int;", output)
    }

    @Test
    fun testVectorAccessTooLargeIndex() {
        withSource("var a = [1, 2, 3][3]; ")
        actParse()
        actInline()
        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;", output)
    }
    @Test
    fun testMissingInAssignment() {
        withSource("var a = a")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testLexicalScope() {
        withSource("var a = 1; var b = {var a = 2; a}")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_3={_2=2;var_2:Int;_2;};var_3:Int;", output)
    }

    @Test
    fun testEvalValues() {
        withSource("var a = -(-(1 + 2) - 3);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=6;var_1:Int;", output)

    }

    @Test
    fun testVar() {
        withSource("var a = 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;", output)
    }

    @Test
    fun testRValueAssignment() {
        withSource("val a = 1; a = 1")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {}
    }

    @Test
    fun testVarWithAdd() {
        withSource("var a = 1; var b = a + 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=1+_1;var_2:Int;", output)
    }

    @Test
    fun testVarInBlock() {
        withSource("var a = {var b = 1; b} + 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_2=1+{_1=1;var_1:Int;_1;};var_2:Int;", output)
    }

    @Test
    fun testFunWithVarArg() {
        withSource("fun a(var b) {b = 2}; a(1);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;{_1=2;}", output)
    }

    @Test
    fun testFunWithArgAndExpr() {
        withSource("fun a(b) = b + 1; var c = a(2);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=3;var_1:Int;", output)
    }

    @Test
    fun testFunWithTypdeVarArgAndExpr() {
        withSource("fun a(var b: Int) = b + 1; var c = a(2);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=2;var_1:Int;_2=1+_1;var_2:Int;", output)
    }

    @Test
    fun testFunWithUntypedVarArgAndExpr() {
        withSource("fun a(var b) = b + 1; var c = a(2);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=2;var_1:Int;_2=1+_1;var_2:Int;", output)
    }

    @Test
    fun testFunWithoutArgsWithBlock() {
        withSource("var a = 1; fun b() { a + 2 }; var c = b();")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=2+_1;var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneMember() {
        withSource("class A { var b = 1 }; var c = A().b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=_1;var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneUntypedArgAndOneMemberInt() {
        withSource("class A(d) { var b = d }; var c = A(1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=_1;var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneUntypedArgAndOneMemberReal() {
        withSource("class A(d) { var b = d }; var c = A(1.1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.1;var_1:Real;_2=_1;var_2:Real;", output)
    }

    @Test
    fun testSimpleCast() {
        withSource("var x: Real = 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;", output)
    }

    @Test
    fun testObjectWithOneTypedArgAndOneMemberReal() {
        withSource("class A(var d: Real) { var b = d }; var c = A(1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=_1;var_2:Real;_3=_2;var_3:Real;", output)
    }

    @Test
    fun testIfElseStmt() {
        withSource("var a = 1; if(a == 1) a = 2 else a = 3")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;if(1==_1)_1=2else_1=3;", output)
    }

    @Test
    fun testIfElseExpr() {
        withSource("var a = 1; a = if(a == 1) 2 else 3")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_1=if(1==_1)2else3;", output)
    }

    @Test
    fun testErrorOnInconvertibleAssignment() {
        withSource("var a: Int = 1; a = 3.2;")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testErrorOnInconvertibleParameter() {
        withSource("fun f(var a: Int) { a + 1 }; var b = f(3.2);")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testErrorOnInconvertibleVarInit() {
        withSource("var a: Int = 3.2;")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testDeclareStuff() {
        withSource("declareScale(1, 2, 3, 4, 5, 6); " +
                "declarePalette(\"Hi\", 1, 1, [0, 0, #ffff0000]);" +
                "declarePalette(\"Hi\", 2, 2, [1, 1, #ffff00ff]);")

        actParse()
        actInline()

        Assert.assertNotNull(scale)
        Assert.assertEquals(2, palettes.size)
    }

    @Test
    fun testSimpleDiff() {
        withSource("var x : Real = 1; var a = diff(x + 2x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=3.0;var_2:Real;", output)
    }

    @Test
    fun testAddDiff() {
        withSource("var x : Real = 1; var a = diff(log x + exp x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=/_1+Exp(_1);var_2:Real;", output)
    }

    @Test
    fun testSubDiff() {
        withSource("var x : Real = 1; var a = diff(log x - exp x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=/_1-Exp(_1);var_2:Real;", output)
    }

    @Test
    fun testMulDiff() {
        withSource("var x : Real = 1; var a = diff(log x * exp x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Exp(_1)/_1+Exp(_1)*Log(_1);var_2:Real;", output)
    }

    @Test
    fun testDivDiff() {
        withSource("var x : Real = 1; var a = diff(log x / exp x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=(Exp(_1)/_1-Log(_1)*Exp(_1))/Exp(_1)^2;var_2:Real;", output)
    }

    @Test
    fun testPowNDiff() {
        withSource("var x : Real = 1; var a = diff(log x ^ 5, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Log(_1)^5*(5.0/_1/Log(_1));var_2:Real;", output)
    }


    @Test
    fun testPowDiff() {
        withSource("var x : Real = 1; var a = diff(x ^ x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=_1^_1*(_1/_1+Log(_1));var_2:Real;", output)
    }

    @Test
    fun testNegDiff() {
        withSource("var x : Real = 1; var a = diff(-x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=-1;var_2:Int;", output)
    }

    @Test
    fun testRecipDiff() {
        withSource("var x : Real = 1; var a = diff(/cosh x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=-(Sinh(_1)/Cosh(_1)^2);var_2:Real;", output)
    }

    @Test
    fun testSqrtDiff() {
        withSource("var x : Real = 1; var a = diff(sqrt cosh x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Sinh(_1)/Sqrt(Cosh(_1));var_2:Real;", output)
    }

    @Test
    fun testLogDiff() {
        withSource("var x : Real = 1; var a = diff(log sin x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Cos(_1)/Sin(_1);var_2:Real;", output)
    }

    @Test
    fun testExpDiff() {
        withSource("var x : Real = 1; var a = diff(exp sin x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Exp(Sin(_1))*Cos(_1);var_2:Real;", output)
    }

    @Test
    fun testSinhDiff() {
        withSource("var x : Real = 1; var a = diff(sinh sin x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Cosh(Sin(_1))*Cos(_1);var_2:Real;", output)
    }


    @Test
    fun testCoshDiff() {
        withSource("var x : Real = 1; var a = diff(cosh sin x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Sinh(Sin(_1))*Cos(_1);var_2:Real;", output)
    }


    @Test
    fun testSinDiff() {
        withSource("var x : Real = 1; var a = diff(sin sinh x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=Cos(Sinh(_1))*Cosh(_1);var_2:Real;", output)
    }


    @Test
    fun testCosDiff() {
        withSource("var x : Real = 1; var a = diff(cos sinh x, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=-(Sin(Sinh(_1))*Cosh(_1));var_2:Real;", output)
    }

    @Test
    fun testDiffDiff() {
        withSource("var x : Real = 1; var a = diff(diff(cos x, x), x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=-Cos(_1);var_2:Real;", output)
    }

    @Test
    fun testNewton() {
        withSource("var x : Real = 1; var a = newton(x^4 - 1, x)")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1.0;var_1:Real;_2=_1-(-1.0+_1^4)/(_1^4*(4.0/_1));var_2:Real;", output)
    }

    @Test
    fun testIfElseBool() {
        withSource("var a = 1; if(if(a == 1) false else true) a = 2 else a = 3")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;if(not1==_1)_1=2else_1=3;", output)
    }

    @Test
    fun testVectorHasNoType() {
        withSource("var c = point; var z = c + [1, -1];")

        actParse()

        try {
            actInline()
            Assert.fail()
        } catch (e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testClassNameHiding() {
        withSource(
                "var f = 2;\n" +
                "class A(var a: Int) {\n" +
                "    var b = a * f + 1;\n" +
                "}\n" +
                "{\n" +
                "    val f = 6;\n" +
                "    val d = A(3);\n" +
                "    var e = d.b;\n" +
                "}\n")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=2;var_1:Int;{_2=3;var_2:Int;_3=1+_2*_1;var_3:Int;_4=_3;var_4:Int;}", output)
    }

    private lateinit var palettes: List<PaletteData>
    private var scale: DoubleArray? = null
    private lateinit var output: String
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    private fun actPrint() {
        output = FractlangParser.program.print(inlined).toString()
    }

    private fun actInline() {
        val rootTable = RootSymbolTable(FractlangProgram.namedInstructions, emptyMap())
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "_$it" }.iterator()

        inlined = ast.accept(
            SemanticAnalysisVisitor(
                rootTable,
                varNameGenerator
            )
        )

        scale = rootTable.defaultScale
        palettes = rootTable.defaultPalettes
    }

    private fun actParse() {
        ast = FractlangParser.program.parse(stream)!!
    }

    private fun withSource(src: String) {
        stream = ParserStream.fromString(src)
    }
}