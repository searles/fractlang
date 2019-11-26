package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.parsing.Trace
class If(trace: Trace, val condition: Node, val thenBranch: Node): Node(trace) {
    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}