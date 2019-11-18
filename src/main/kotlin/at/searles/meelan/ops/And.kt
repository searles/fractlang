package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object And: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool, BaseTypes.Bool)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return BoolNode(trace, (args[0] as BoolNode).value && (args[1] as BoolNode).value)
    }
}