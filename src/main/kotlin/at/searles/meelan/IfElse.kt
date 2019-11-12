package at.searles.meelan

import at.searles.parsing.Trace
class IfElse(trace: Trace, val condition: Node, val thenBranch: Node, val elseBranch: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}