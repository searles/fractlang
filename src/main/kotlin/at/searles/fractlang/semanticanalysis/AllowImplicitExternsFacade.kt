package at.searles.fractlang.semanticanalysis

import at.searles.fractlang.SymbolTable
import at.searles.fractlang.nodes.ExternDecl
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

class AllowImplicitExternsFacade(val owner: String, val analyzer: SemanticAnalysisVisitor) : SymbolTable {
    override fun get(trace: Trace, id: String): Node? {
        val node = analyzer.table[trace, id]

        if(node == null) {
            ExternDecl(trace, id, "-> $owner", "0").accept(analyzer)
            return analyzer.table[trace, id] ?: error("undefined implicit extern")
        }

        return node
    }

    override fun addExternValue(trace: Trace, name: String, description: String, expr: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val defaultExpr = "0"
    }
}
