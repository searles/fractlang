package at.searles.meelan.nodes

import at.searles.meelan.Visitor
import at.searles.parsing.Trace

class BoolNode(trace: Trace, val value: Boolean): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
