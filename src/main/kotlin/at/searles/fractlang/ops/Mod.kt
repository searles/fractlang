package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Mod: HasSpecialSyntax, StandardOp(3,
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int)
) {
    private fun imod(a: Int, b: Int): Int {
        val m = a % b
        return if(m < 0) m + b else  m
    }

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args[0] is IntNode && args[1] is IntNode) {
            return IntNode(trace, imod((args[0] as IntNode).value, (args[1] as IntNode).value))
        }

        return createApp(trace, args)
    }
}