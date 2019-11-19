package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.parsing.Trace

class BoolNode(trace: Trace, val value: Boolean): Node(trace) {
    init {
        type = BaseTypes.Bool
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
