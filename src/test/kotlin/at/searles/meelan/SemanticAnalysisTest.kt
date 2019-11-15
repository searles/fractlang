package at.searles.meelan

import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree
import org.junit.Assert
import org.junit.Test

class SemanticAnalysisTest {
    private lateinit var output: String
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream

    @Test
    fun testVar() {
        withSource("var a = 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;", output)
    }

    @Test
    fun testVarWithAdd() {
        withSource("var a = 1; var b = a + 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=_1+1;var_2:Int;", output)
    }

    @Test
    fun testVarInBlock() {
        withSource("var a = {var b = 1; b} + 1")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=_1+1;var_2:Int;", output)
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

        Assert.assertEquals("_1=2+1;var_1:Int;", output)
    }

    @Test
    fun testFunWithTypdeVarArgAndExpr() {
        withSource("fun a(var b: Int) = b + 1; var c = a(2);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=2;var_1:Int;_2=_1+1;var_2:Int;", output)
    }

    @Test
    fun testFunWithUntypedVarArgAndExpr() {
        withSource("fun a(var b) = b + 1; var c = a(2);")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=2;var_1:Int;_2=_1+1;var_2:Int;", output)
    }

    @Test
    fun testFunWithoutArgsWithBlock() {
        withSource("var a = 1; fun b() { a + 2 }; var c = b();")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2={_1+2;};var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneMember() {
        withSource("class A { var b = 1 }; var c = A().b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2={_1+2;};var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneUntypedArgAndOneMemberInt() {
        withSource("class A(d) { var b = d }; var c = A(1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2={_1+2;};var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneUntypedArgAndOneMemberReal() {
        withSource("class A(d) { var b = d }; var c = A(1.1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2={_1+2;};var_2:Int;", output)
    }

    @Test
    fun testObjectWithOneTypedArgAndOneMemberReal() {
        withSource("class A(d: Real) { var b = d }; var c = A(1).b;")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2={_1+2;};var_2:Int;", output)
    }

    @Test
    fun test() {
        withSource("// how does this work?\n" +
                "var f = 2;\n" +
                "class A(var a: Int) {\n" +
                "var b = a * f + 1;\n" +
                "}\n" +
                "{\n" +
                "def f = 6;\n" +
                "var d = A(3);\n" +
                "var e = d.b;\n" +
                "}\n")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("", output)
    }

    private fun actPrint() {
        output = Meelan.program.print(inlined).toString()
    }

    private fun actInline() {
        val rootTable = object: SymbolTable {
            override fun get(id: String): Node? {
                return null
            }
        }

        val varNameGenerator = generateSequence(1) { it + 1 }.map { "_$it" }.iterator()

        inlined = ast.accept(InlineVisitor(rootTable, varNameGenerator))
    }

    private fun actParse() {
        ast = Meelan.program.parse(stream)!!
    }

    private fun withSource(src: String) {
        stream = ParserStream.fromString(src)
    }
}