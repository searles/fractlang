package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.parsing.Trace

class BoolNode(trace: Trace, val value: Boolean): Node(trace) {
    init {
        type = BaseTypes.Bool
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "$value"
    }
}
