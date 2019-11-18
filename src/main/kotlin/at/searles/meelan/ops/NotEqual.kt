package at.searles.meelan.ops

import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object NotEqual: HasSpecialSyntax, Op {
    override fun apply(trace: Trace, args: List<Node>): Node {
        return Not.apply(trace, listOf(Equal.apply(trace, args)))
    }
}