package at.searles.meelan.nodes

import at.searles.meelan.Visitor
import at.searles.parsing.Trace
class IdNode(trace: Trace, val id: String): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}