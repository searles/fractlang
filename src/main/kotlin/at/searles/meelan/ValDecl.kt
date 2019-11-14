package at.searles.meelan

import at.searles.parsing.Trace
class ValDecl(trace: Trace, val name: String, val init: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}