package at.searles.fractlang

import at.searles.commons.math.Cplx
import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.linear.LinearizeStmt
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.RealNode
import at.searles.fractlang.ops.*
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Op
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.fractlang.vm.VmCodeAssembler
import at.searles.parsing.ParserStream

class CompilerInstance(private val sourceCodeStream: ParserStream,
                       instructions: Map<String, Op>,
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

    val scale = symbolTable.scale

    val palettes = symbolTable.palettes

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
        val vmInstructions: List<VmBaseOp> = listOf(Add, Sub, Mul, Div, Mod, Pow, Neg,
            Reciprocal, Abs, Assign, Jump, Equal, Less, Next,
            Sqrt, Exp, Log, Sin, Cos, Sinh, Cosh,
            ToReal, Cons,
            Rad, Arc, RealPart, ImaginaryPart, Conj, Rabs, Iabs,
            Point, SetResult
            )

        val namedInstructions = mapOf(
            "next" to Next,
            "abs" to Abs,
            "neg" to Neg,
            "rec" to Reciprocal,
            "sqrt" to Sqrt,
            "exp" to Exp,
            "log" to Log,
            "sin" to Sin,
            "cos" to Cos,
            "sinh" to Sinh,
            "cosh" to Cosh,
            "conj" to Conj,
            "rabs" to Rabs,
            "iabs" to Iabs,
            "re" to RealPart,
            "im" to ImaginaryPart,
            "rad" to Rad,
            "arc" to Arc,
            "point" to Point,
            "setResult" to SetResult,
            "toReal" to ToReal,
            "pi" to ConstOp { RealNode(it, Math.PI) },
            "tau" to ConstOp { RealNode(it, 2 * Math.PI) },
            "e" to ConstOp { RealNode(it, Math.E) },
            "i" to ConstOp { CplxNode(it, Cplx(0.0, 1.0)) }
        )
    }
}