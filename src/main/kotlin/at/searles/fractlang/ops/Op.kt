package at.searles.fractlang.ops

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.OpNode
import at.searles.parsing.Trace

interface Op {
    fun toNode(trace: Trace): Node {
        return OpNode(trace, this)
    }

    fun apply(trace: Trace, args: List<Node>): Node
}
