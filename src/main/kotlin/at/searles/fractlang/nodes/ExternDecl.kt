package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Trace

/**
 * The description might be modified during inlining.
 */
class ExternDecl(trace: Trace, val name: String, val description: Node, val expr: String): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "extern $name : $description = $expr"
    }
}
