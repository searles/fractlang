package at.searles.meelan

import at.searles.meelan.ops.Op
import at.searles.parsing.Trace

class App(trace: Trace, val head: Node, val args: List<Node>): Node(trace) {

    constructor(trace: Trace, head: Op, args: List<Node>):
            this(trace, head.toNode(trace), args)

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}