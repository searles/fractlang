package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Mod: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> IntNode(trace, arg0.value % (args[1] as IntNode).value)
            else -> throw IllegalArgumentException()
        }
    }
}