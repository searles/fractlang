package at.searles.meelan

import at.searles.parsing.Trace
class Block(trace: Trace, val stmts: List<Node>): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}