package at.searles.fractlang.ops

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.OpNode
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.parsing.Trace

interface Op: MetaOp {
    fun toNode(trace: Trace): Node {
        return OpNode(trace, this)
    }

    override fun inlineApply(trace: Trace, visitor: SemanticAnalysisVisitor, args: List<Node>): Node {
        return apply(trace, args.map{it.accept(visitor)})
    }

    fun apply(trace: Trace, args: List<Node>): Node
}
