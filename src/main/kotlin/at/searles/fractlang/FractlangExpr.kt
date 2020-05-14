package at.searles.fractlang

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.parsing.FractlangParser
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.parsing.ParserStream

object FractlangExpr {
    fun fromString(expr: String): Node {
        val sourceCodeStream = ParserStream.fromString(expr)

        val ast = FractlangParser.program.parse(sourceCodeStream)
            ?: throw SemanticAnalysisException("Could not parse program", sourceCodeStream.createTrace())

        if(!FractlangParser.eof.recognize(sourceCodeStream)) {
            throw SemanticAnalysisException("Expression not fully parsed", sourceCodeStream.createTrace())
        }

        val symbolTable = RootSymbolTable(FractlangProgram.namedInstructions, emptyMap())
        val varNameGenerator = NameGenerator()

        return ast.accept(SemanticAnalysisVisitor(symbolTable, varNameGenerator))
    }
}