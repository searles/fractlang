package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.Optimizer
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Abs: HasSpecialSyntax, StandardOp (
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return Optimizer.abs(trace, args)
    }
}