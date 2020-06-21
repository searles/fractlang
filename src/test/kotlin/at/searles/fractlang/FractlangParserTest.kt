package at.searles.fractlang

import at.searles.buf.ReaderCharStream
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.parsing.FractlangGrammar
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.lexer.TokenStream
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test
import java.io.FileReader

class FractlangGrammarTest {
    /*
    // what is returned:
value.x/value.y are normalized to the range 0-1.
layer is then used in value.x where layer is also normalized.
Thus, in total 3 values are returned.

float3(value.x [with layer], value.y, height) These values are then also stored.
     */

    @Test
    fun testSimpleMandel() {
        val filename = "src/test/resources/mandelbrot.ft"
        val input = ParserStream(TokenStream.fromCharStream(ReaderCharStream(FileReader(filename))))
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertNotNull(source)
    }

    @Test
    fun testChainAppWithParens() {
        val input = ParserStream.fromString("a b(c, d)")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("a(b(c,d))", source?.toString())
    }

    @Test
    fun testAddendBug() {
        // This was a bug in a beta version
        val filename = "src/test/resources/addend.ft"
        val input = ParserStream(TokenStream.fromCharStream(ReaderCharStream(FileReader(filename))))
        FractlangGrammar.program.parse(input)
    }

    @Test
    fun testTrueAsExpr() {
        val input = ParserStream.fromString("true")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("true", source?.toString())
    }

    @Test
    fun testIfElseBool() {
        val input = ParserStream.fromString("if(if(a == 1) false else true) a = 2 else a = 3")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("if(if(a==1)falseelsetrue)a=2elsea=3;", source?.toString())
    }

    @Test
    fun testIfElseExprBool() {
        val input = ParserStream.fromString("if(a == 1) false else true")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("if(a==1)falseelsetrue", source?.toString())
    }

    @Test
    fun testIfElseExpr() {
        val input = ParserStream.fromString("if(a == 1) 1 else 2")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("if(a==1)1else2", source?.toString())
    }

    @Test
    fun testQualifiedFnCall() {
        val input = ParserStream.fromString("a.at(1)")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("a.at(1)", source?.toString())
    }

    @Test
    fun testQualifiedWithVector() {
        val input = ParserStream.fromString("[1, 2].size")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("[1,2].size", source?.toString())
    }

    @Test
    fun testUntypedVar() {
        val input = ParserStream.fromString("var a = 1")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vara=1;", source?.toString())
    }

    @Test
    fun testAbsVar() {
        val input = ParserStream.fromString("var a = |1|")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vara=|1|;", source?.toString())
    }

    @Test
    fun testAbsAdd() {
        val input = ParserStream.fromString("var a = |1|+|2-|3||")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vara=|1|+|2-|3||;", source?.toString())
    }

    @Test
    fun testTypedVar() {
        val input = ParserStream.fromString("var a: Int")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vara:Int;", source?.toString())
    }

    @Test
    fun testFunConst() {
        val input = ParserStream.fromString("fun a() = 1")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa()=1;", source?.toString())
    }


    @Test
    fun testValConst() {
        val input = ParserStream.fromString("val a = 1")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vala=1;", source?.toString())
    }

    @Test
    fun testFunWithNoArg() {
        val input = ParserStream.fromString("fun a() = 1")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa()=1;", source?.toString())
    }

    @Test
    fun testFunWithArg() {
        val input = ParserStream.fromString("fun a(b) = b")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa(b)=b;", source?.toString())
    }

    @Test
    fun testQualifiedFunCall() {
        val input = ParserStream.fromString("var v = a(d).b(c)")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("a.bc;", source?.toString())
    }

    @Test
    fun testFunWithUntypedVarArg() {
        val input = ParserStream.fromString("fun a(var b) = b")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa(varb)=b;", source?.toString())
    }


    @Test
    fun testFunWithTypedVarArg() {
        val input = ParserStream.fromString("fun a(var b: Int) = b")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa(varb:Int)=b;", source?.toString())
    }

    @Test
    fun testBlock() {
        val input = ParserStream.fromString("{ var a = 1; }")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("{vara=1;}", source?.toString())
    }

    @Test
    fun testFunConstWithBlock() {
        val input = ParserStream.fromString("fun a() { var a = 1; }")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("funa(){vara=1;}", source?.toString())
    }

    @Test
    fun testClassWithTypedVarArg() {
        val input = ParserStream.fromString("class a(var b: Int) { var c: Int = b  }")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("classa(varb:Int){varc:Int=b;}", source?.toString())
    }

    @Test
    fun testQualified() {
        val input = ParserStream.fromString("class a(var b: Int) { var c: Int = b  }; var d = a(1).c;")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("classa(varb:Int){varc:Int=b;}vard=a(1).c;", source?.toString())
    }

    @Test
    fun testAbsExpr() {
        val input = ParserStream.fromString("|a|")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("|a|", source?.toString())
    }

    @Test
    fun testNegAddExpr() {
        val input = ParserStream.fromString("-(1+2)")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("-(1+2)", source?.toString())
    }

    @Test
    fun testErrorOnLongIntl() {
        val input = ParserStream.fromString("1234223432344234")

        try {
            FractlangGrammar.expr.parse(input)
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            //
        }
    }

    @Test
    fun testLongHex() {
        val input = ParserStream.fromString("#ffffffff")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertTrue(ast is IntNode)
        Assert.assertEquals("-1", source?.toString())
    }

    @Test
    fun testAddMulExpr() {
        val input = ParserStream.fromString("(1*2)+(3*4)")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("1*2+3*4", source?.toString())
    }

    @Test
    fun testConsQualifier() {
        val input = ParserStream.fromString("(1:2).x")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("(1:2).x", source?.toString())
    }

    @Test
    fun testQualifierFunCall() {
        val input = ParserStream.fromString("f.x (1, 2)")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("f.x(1,2)", source?.toString())
    }

    @Test
    fun testVector() {
        val input = ParserStream.fromString("[1, 2, 3]")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("[1,2,3]", source?.toString())
    }

    @Test
    fun testArrayAccess() {
        val input = ParserStream.fromString("a[1]")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("a[1]", source?.toString())
    }

    @Test
    fun testSimpleApp() {
        val input = ParserStream.fromString("sin x")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("sin(x)", source?.toString())
    }

    @Test
    fun testSimpleAppApp() {
        val input = ParserStream.fromString("sin cos x")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("sin(cos(x))", source?.toString())
    }

    @Test
    fun testIndexed() {
        val input = ParserStream.fromString("a[1]")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("a[1]", source?.toString())
    }

    @Test
    fun testAppThenParenthesisThenApp() {
        val input = ParserStream.fromString("x (y+1) z")
        val ast = FractlangGrammar.expr.parse(input)
        val source = FractlangGrammar.expr.print(ast)

        Assert.assertEquals("x((y+1)(z))", source?.toString())
    }

    @Test
    fun testWhileNoBody() {
        val input = ParserStream.fromString("while (1 == 1);")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("while(1==1);", source?.toString())
    }

    @Test
    fun testSimpleAlgorithm() {
        val input = ParserStream.fromString("var i = 0; var sum = 0; while(i < 10) {sum = sum + i; i = i + 1}")
        val ast = FractlangGrammar.program.parse(input)
        val source = FractlangGrammar.program.print(ast)

        Assert.assertEquals("vari=0;varsum=0;while(i<10){sum=sum+i;i=i+1;};", source?.toString())
    }

    @Test
    fun testErrorMessages() {
        val input = ParserStream.fromString("var i = 1 + 2 + ")

        try {
            FractlangGrammar.program.parse(input)
            Assert.fail()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}
