package at.searles.fractlang

import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.ops.*
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Op
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.fractlang.vm.VmCodeAssembler
import at.searles.parsing.ParserStream

class CompilerInstance(private val sourceCodeStream: ParserStream,
                       private val instructions: Map<String, Op>,
                       externValues: Map<String, String> = emptyMap()) {
    constructor(sourceCode: String, externValues: Map<String, String>):
            this(ParserStream.fromString(sourceCode), namedInstructions, externValues)

    private val symbolTable = RootSymbolTable(instructions, externValues)
    private val varNameGenerator = generateSequence(0) { it + 1 }.map { "\$$it" }.iterator()

    private lateinit var typedAst: Node
    private lateinit var linearizedCode: ArrayList<CodeLine>
    private lateinit var vmCodeAssembler: VmCodeAssembler

    val vmCode: List<Int>
        get() = vmCodeAssembler.vmCode

    val externValues: Map<String, String>
            get() = symbolTable.externValues

    fun analyzeExpr() {
        val ast = FractlangParser.expr.parse(sourceCodeStream)
            ?: throw SemanticAnalysisException("Could not parse program", sourceCodeStream.createTrace())

        if(!FractlangParser.eof.recognize(sourceCodeStream)) {
            throw SemanticAnalysisException("Program not fully parsed", sourceCodeStream.createTrace())
        }

        typedAst = ast.accept(SemanticAnalysisVisitor(symbolTable, varNameGenerator))
    }

    fun analyze() {
        val ast = FractlangParser.program.parse(sourceCodeStream)
            ?: throw SemanticAnalysisException("Could not parse program", sourceCodeStream.createTrace())

        if(!FractlangParser.eof.recognize(sourceCodeStream)) {
            throw SemanticAnalysisException("Program not fully parsed", sourceCodeStream.createTrace())
        }

        typedAst = ast.accept(SemanticAnalysisVisitor(symbolTable, varNameGenerator))
    }

    fun compile() {
        analyze()
        linearizedCode = ArrayList()
        typedAst.accept(LinearizeStmt(linearizedCode, varNameGenerator))
        vmCodeAssembler = VmCodeAssembler(linearizedCode, vmInstructions)
    }

    companion object {
        val vmInstructions = listOf(Add, Sub, Mul, Div, Mod, Neg, Reciprocal, Abs, Assign, Jump, Equal, Less, RealPart, ImaginaryPart, Point, SetResult)
        val namedInstructions = mapOf(
            "abs" to Abs,
            "neg" to Neg,
            "rec" to Reciprocal,
            "re" to RealPart,
            "im" to ImaginaryPart,
            "point" to Point,
            "setResult" to SetResult
        )
    }
}