package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.App
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

/**
 * Requires as input a layer
 */
object SetResult: StandardOp(Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Cplx, BaseTypes.Real)) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return App(trace, this, args).apply {
            type = BaseTypes.Unit
        }
    }
}