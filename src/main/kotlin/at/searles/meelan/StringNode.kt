package at.searles.meelan

import at.searles.parsing.Trace
class StringNode(trace: Trace, val string: String): Node(trace) {
    init {
        type = BaseTypes.String
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}