package at.searles.fractlang.semanticanalysis

import at.searles.fractlang.PaletteData
import at.searles.fractlang.SymbolTable
import at.searles.fractlang.nodes.ExternDecl
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.StringNode
import at.searles.parsing.Trace

/**
 * When parsing externs, this one is used to allow implicit extern definitions.
 */
class AllowImplicitExternsFacade(private val owner: String, private val analyzer: SemanticAnalysisVisitor) : SymbolTable {
    override fun get(trace: Trace, id: String): Node? {
        val node = analyzer.table[trace, id]

        if(node == null) {
            ExternDecl(trace, id, StringNode(trace, "$id (defined in $owner)"), defaultExpr).accept(analyzer)
            return analyzer.table[trace, id] ?: error("undefined implicit extern")
        }

        return node
    }

    override fun addExternValue(trace: Trace, name: String, description: String, expr: String) {
        analyzer.table.addExternValue(trace, name, description, expr)
    }

    override fun setScale(scaleArray: DoubleArray) {
        analyzer.table.setScale(scaleArray)
    }

    override fun addPalette(paletteData: PaletteData) {
        analyzer.table.addPalette(paletteData)
    }

    companion object {
        const val defaultExpr = "0"
    }
}
