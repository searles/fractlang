package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.parsing.Trace

class Assignment(trace: Trace, val lhs: Node, val rhs: Node): Node(trace) {

    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}