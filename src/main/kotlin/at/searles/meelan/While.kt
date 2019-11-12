package at.searles.meelan

import at.searles.parsing.Trace

class While(trace: Trace, val condition: Node, val body: Node?): Node(trace) {
    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}