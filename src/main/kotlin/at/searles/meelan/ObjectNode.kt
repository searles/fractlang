package at.searles.meelan

import at.searles.parsing.Trace

class ObjectNode(trace: Trace, private val table: Map<String, Node>) : Node(trace) {
    fun get(trace: Trace, qualifier: String): Node {
        return table[qualifier] ?: throw SemanticAnalysisException("no such member", trace)
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
