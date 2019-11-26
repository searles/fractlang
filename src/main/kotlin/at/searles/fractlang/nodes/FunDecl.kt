package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Trace
class FunDecl(trace: Trace, val name: String, val parameters: List<Node>, val body: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}