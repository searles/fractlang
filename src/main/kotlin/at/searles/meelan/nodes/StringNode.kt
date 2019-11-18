package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.parsing.Trace
class StringNode(trace: Trace, val string: String): Node(trace) {
    init {
        type = BaseTypes.String
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}