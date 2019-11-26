package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Mod: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> IntNode(trace, arg0.value % (args[1] as IntNode).value)
            else -> throw IllegalArgumentException()
        }
    }
}