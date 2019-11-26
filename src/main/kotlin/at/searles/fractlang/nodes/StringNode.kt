package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.parsing.Trace
class StringNode(trace: Trace, val string: String): Node(trace) {
    init {
        type = BaseTypes.Obj
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}