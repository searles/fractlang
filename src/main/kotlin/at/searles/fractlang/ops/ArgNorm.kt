package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace
import kotlin.math.PI

object ArgNorm: BaseOp (
    Signature(BaseTypes.Real, BaseTypes.Cplx)
) {
    private const val tau = 2 * PI
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return if(args[0] is CplxNode)
            RealNode(trace, (args[0] as CplxNode).value.arg() / (2 * PI))
        else
            Div.apply(trace,
                Arg.createApp(trace, args[0]),
                RealNode(trace, tau)
            )
    }
}