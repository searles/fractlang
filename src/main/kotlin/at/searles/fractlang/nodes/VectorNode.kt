package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

/**
 * Is removed during semantic analysis?
 */
class VectorNode(trace: Trace, val items: List<Node>): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
