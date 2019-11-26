package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
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
