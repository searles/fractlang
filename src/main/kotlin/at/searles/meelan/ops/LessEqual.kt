package at.searles.meelan.ops

import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object LessEqual: HasSpecialSyntax, Op {
    override fun apply(trace: Trace, args: List<Node>): Node {
        return Not.apply(trace, listOf(Less.apply(trace, listOf(args[1], args[0]))))
    }
}