package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace
import kotlin.math.ln

/**
 * log(x + 1)
 */
object Log1P: BaseOp (
    Signature(BaseTypes.Real, BaseTypes.Real)
) {

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when {
            args[0] is RealNode -> RealNode(trace, ln((args[0] as RealNode).value + 1))
            else -> Log.createApp(trace,
                Add.createApp(trace,
                    RealNode(trace, 1.0),
                    args[0]
                )
            )
        }
    }
}