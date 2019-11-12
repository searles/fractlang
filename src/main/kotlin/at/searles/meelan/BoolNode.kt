package at.searles.meelan

import at.searles.parsing.Trace

class BoolNode(trace: Trace, value: Boolean): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
