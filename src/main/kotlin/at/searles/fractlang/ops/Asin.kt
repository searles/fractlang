package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.RealNode
import at.searles.parsing.Trace
import kotlin.math.asin

object Asin: StandardOp (1,
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when {
            args[0] is RealNode -> RealNode(trace, asin((args[0] as RealNode).value))
            args[0] is CplxNode -> CplxNode(trace, Cplx().asin((args[0] as CplxNode).value))
            else -> createApp(trace, args)
        }
    }
}