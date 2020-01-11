package at.searles.fractlang

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ExternTest {
    @Test
    fun testSimpleExtern() {
        withSource(
            "extern a: \"A\" = \"1\"; var b = a")

        actParse()
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;", output)
    }

    @Test
    fun testInlinedExtern() {
        withSource(
            "var a = 1; extern e: \"A\" = \"if(c < a) d else 4 \"; var b = e")

        actParse()

        actAddExtern("d", "f + g")
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=if(0<_1)0else4;var_2:Int;", output)

        Assert.assertTrue(rootTable.activeParameters.containsKey("c"))
        Assert.assertTrue(rootTable.activeParameters.containsKey("d"))

    }

    @Test
    fun testTransitiveInlinedExtern() {
        withSource(
            "var a = 1; extern e: \"A\" = \"if(c < a) d else 4 \"; var b = e")

        actParse()

        actAddExtern("d", "f + 17")
        actInline()

        actPrint()

        Assert.assertEquals("_1=1;var_1:Int;_2=if(0<_1)17else4;var_2:Int;", output)

        Assert.assertTrue(rootTable.activeParameters.containsKey("c"))
        Assert.assertTrue(rootTable.activeParameters.containsKey("f"))

    }

    @Test
    fun testDescriptionSeparate() {
        withSource("val descA = \"Some very long text that explains a.\";" +
                "extern a: descA = \"1\"; " +
                "var b = a;")

        actParse()
        actInline()

        Assert.assertTrue(rootTable.activeParameters.containsKey("a"))
        Assert.assertTrue(rootTable.descriptionMap.containsKey("a"))
    }

    private lateinit var output: String
    private lateinit var inlined: Node
    private lateinit var ast: Node
    private lateinit var stream: ParserStream
    private lateinit var rootTable: RootSymbolTable
    private lateinit var externs: HashMap<String, String>

    @Before
    fun setUp() {
        externs = HashMap()
    }

    private fun actAddExtern(name: String, value: String) {
        externs[name] = value
    }

    private fun actPrint() {
        output = FractlangParser.program.print(inlined).toString()
    }

    private fun actInline() {
        rootTable = RootSymbolTable(emptyMap(), externs)
        val varNameGenerator = generateSequence(1) { it + 1 }.map { "_$it" }.iterator()

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