package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.BoolNode
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Equal: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args.all{ it is IntNode }) {
            return BoolNode(trace, (args[0] as IntNode).value == (args[1] as IntNode).value)
        }

        return createTypedApp(trace, args)
    }
}