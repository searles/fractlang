package at.searles.meelan

import at.searles.parsing.Trace

/**
 * This node represents a no-operation node and is a null-object for nodes.
 */
class Nop(trace: Trace) : Node(trace) {

    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
