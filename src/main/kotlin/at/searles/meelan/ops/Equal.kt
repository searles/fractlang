package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Equal: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return BoolNode(trace, (args[0] as IntNode).value == (args[1] as IntNode).value)
    }
}