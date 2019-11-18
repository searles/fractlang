package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Not: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return BoolNode(trace, !(args[0] as BoolNode).value)
    }
}