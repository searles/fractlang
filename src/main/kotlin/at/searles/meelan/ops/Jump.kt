package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Jump: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        throw SemanticAnalysisException("cannot apply jump at this stage", trace)
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        throw SemanticAnalysisException("cannot apply jump at this stage", trace)
    }

    // TODO
}