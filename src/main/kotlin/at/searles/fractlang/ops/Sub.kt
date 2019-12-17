package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.RealNode
import at.searles.parsing.Trace

object Sub: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> IntNode(trace, arg0.value - (args[1] as IntNode).value)
            is RealNode -> RealNode(trace, arg0.value - (args[1] as RealNode).value)
            is CplxNode -> CplxNode(trace, Cplx().sub(arg0.value, (args[1] as CplxNode).value))
            else -> throw IllegalArgumentException()
        }
    }
}