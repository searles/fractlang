package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.BoolNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Not: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        if(args[0] is BoolNode) return BoolNode(trace, !(args[0] as BoolNode).value)
        return app(trace, signature, args)
    }
}