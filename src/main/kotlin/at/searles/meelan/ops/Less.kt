package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object Less: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> BoolNode(trace, arg0.value < (args[1] as IntNode).value)
            is RealNode -> BoolNode(trace, arg0.value < (args[1] as RealNode).value)
            else -> throw IllegalArgumentException()
        }
    }
}