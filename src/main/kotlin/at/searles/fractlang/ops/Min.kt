package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.NumValue
import at.searles.fractlang.nodes.RealNode
import at.searles.parsing.Trace
import kotlin.math.min

object Min: StandardOp (2,
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args[0] is NumValue && args[1] is NumValue) {
            return when(val arg0 = args[0]) {
                is RealNode -> RealNode(trace, min(arg0.value, (args[1] as RealNode).value))
                is CplxNode -> CplxNode(trace, Cplx().min(arg0.value, (args[1] as CplxNode).value))
                else -> error("something went wrong with the cast")
            }
        }

        if(args[1] is NumValue) return evaluate(
            trace,
            listOf(args[1], args[0])
        )

        return createApp(trace, args)
    }
}