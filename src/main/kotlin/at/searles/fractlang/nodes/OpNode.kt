package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.fractlang.ops.Op
import at.searles.parsing.Trace

class OpNode(trace: Trace, val op: Op) : Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
