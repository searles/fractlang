package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.parsing.Trace

/**
 * Is removed during semantic analysis?
 */
class VectorNode(trace: Trace, val items: List<Node>): Node(trace) {

    init {
        type = BaseTypes.Obj
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "[${items.joinToString(", ")}]"
    }
}
