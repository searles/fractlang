package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object Neg: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when(val arg = args[0]) {
            is IntNode -> IntNode(trace, -arg.value)
            is RealNode -> RealNode(trace, -arg.value)
            is CplxNode -> CplxNode(trace, Cplx().neg(arg.value))
            else -> throw IllegalArgumentException()
        }
    }
}