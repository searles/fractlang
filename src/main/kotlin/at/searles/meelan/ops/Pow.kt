package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import kotlin.math.pow

object Pow: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Int),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> IntNode(trace, pow(arg0.value, (args[1] as IntNode).value))
            is RealNode ->
                when(val arg1 = args[1]) {
                    is IntNode -> RealNode(trace, pow(arg0.value, arg1.value))
                    is RealNode -> RealNode(trace, arg0.value.pow(arg1.value))
                    else -> throw IllegalArgumentException()
                }
            is CplxNode ->
                when(val arg1 = args[1]) {
                    is IntNode -> CplxNode(trace, Cplx().powInt(arg0.value, arg1.value))
                    is RealNode -> CplxNode(trace, Cplx().pow(arg0.value, Cplx(arg1.value)))
                    is CplxNode -> CplxNode(trace, Cplx().pow(arg0.value, arg1.value))
                    else -> throw IllegalArgumentException()
                }
            else -> throw IllegalArgumentException()
        }
    }

    fun pow(base: Int, exp: Int): Int {
        // TODO
        return base.toDouble().pow(exp).toInt()
    }

    fun pow(base: Double, exp: Int): Double {
        // TODO
        return base.pow(exp)
    }
}