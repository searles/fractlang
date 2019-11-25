package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.*
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object Less: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        if(args.all{ it is NumValue }) {
            return when (val arg0 = args[0]) {
                is IntNode -> BoolNode(trace, arg0.value < (args[1] as IntNode).value)
                is RealNode -> BoolNode(trace, arg0.value < (args[1] as RealNode).value)
                else -> throw IllegalArgumentException()
            }
        }

        return app(trace, signature, args)
    }
}