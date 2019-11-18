package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace

object Cons: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Cplx, BaseTypes.Real, BaseTypes.Real)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return CplxNode(trace, Cplx((args[0] as RealNode).value, (args[1] as RealNode).value))
    }
}