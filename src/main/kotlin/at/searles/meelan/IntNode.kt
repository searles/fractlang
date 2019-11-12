package at.searles.meelan

import at.searles.parsing.Trace
class IntNode(trace: Trace, val value: Int) : Node(trace) {
    init {
        type = BaseTypes.Integer
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
