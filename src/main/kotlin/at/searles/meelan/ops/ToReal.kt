package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

object ToReal: BaseOp(
    Signature(BaseTypes.Real, BaseTypes.Int)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return when(val arg = args[0]) {
            is IntNode -> RealNode(trace, arg.value.toDouble())
            else -> throw IllegalArgumentException()
        }
    }
}