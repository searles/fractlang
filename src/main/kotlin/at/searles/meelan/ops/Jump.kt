package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException
import kotlin.math.abs

object Jump: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return when(val arg = args[0]) {
            is IntNode -> IntNode(trace, abs(arg.value))
            is RealNode -> RealNode(trace, abs(arg.value))
            is CplxNode -> CplxNode(trace, Cplx().abs(arg.value))
            else -> throw IllegalArgumentException()
        }
    }
}