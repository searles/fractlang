package at.searles.meelan

import at.searles.parsing.Trace

class App(trace: Trace, val head: Node, val args: List<Node>): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}