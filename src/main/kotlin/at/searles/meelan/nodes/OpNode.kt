package at.searles.meelan.nodes

import at.searles.meelan.Visitor
import at.searles.meelan.ops.Op
import at.searles.parsing.Trace

class OpNode(trace: Trace, val op: Op) : Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
