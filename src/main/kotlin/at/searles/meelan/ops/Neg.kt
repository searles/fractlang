package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object Neg: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return when(val arg = args[0]) {
            is IntNode -> IntNode(trace, -arg.value)
            is RealNode -> RealNode(trace, -arg.value)
            is CplxNode -> CplxNode(trace, Cplx().neg(arg.value))
            else -> throw IllegalArgumentException()
        }
    }
}