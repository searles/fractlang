package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Xor: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool, BaseTypes.Bool)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        for (i in 0 .. 1) {
            if (args[i] is BoolNode) {
                return if((args[i] as BoolNode).value)
                    Not.apply(trace, listOf(args[1 - i]))
                else
                    args[1 - i]
            }
        }

        return app(trace, signature, args)
    }
}