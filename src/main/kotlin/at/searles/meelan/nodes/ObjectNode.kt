package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.Visitor
import at.searles.parsing.Trace

class ObjectNode(trace: Trace, private val table: Map<String, Node>) : HasMembers, Node(trace) {
    init {
        type = BaseTypes.Unit
    }

    override fun getMember(trace: Trace, memberId: String): Node {
        return table[memberId] ?: throw SemanticAnalysisException("no such member", trace)
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
