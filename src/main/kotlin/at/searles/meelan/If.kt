package at.searles.meelan

import at.searles.parsing.Trace
class If(trace: Trace, val condition: Node, val thenBranch: Node): Node(trace) {
    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}