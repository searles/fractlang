package at.searles.meelan.ops

import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.OpNode
import at.searles.parsing.Trace

interface Op {
    fun toNode(trace: Trace): Node {
        return OpNode(trace, this)
    }

    fun apply(trace: Trace, args: List<Node>): Node
}
