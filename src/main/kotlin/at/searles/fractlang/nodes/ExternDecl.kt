package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Trace

class ExternDecl(trace: Trace, val name: String, val description: String, val expr: String): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
