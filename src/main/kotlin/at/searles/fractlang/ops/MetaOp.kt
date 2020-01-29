package at.searles.fractlang.ops

import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

interface MetaOp {
    fun inlineApply(trace: Trace, visitor: SemanticAnalysisVisitor, args: List<Node>): Node
}