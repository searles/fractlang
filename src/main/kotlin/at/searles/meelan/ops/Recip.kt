package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object Recip: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return when(val arg = args[0]) {
            is RealNode -> RealNode(trace, 1.0 / arg.value)
            is CplxNode -> CplxNode(trace, Cplx().rec(arg.value))
            else -> throw IllegalArgumentException()
        }
    }
}