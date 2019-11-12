package at.searles.meelan

import at.searles.parsing.Trace
class QualifiedNode(trace: Trace, val instance: Node, val qualifier: String) : Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
