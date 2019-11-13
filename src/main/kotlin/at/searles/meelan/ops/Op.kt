package at.searles.meelan.ops

import at.searles.meelan.Node
import at.searles.meelan.OpNode
import at.searles.parsing.Trace

interface Op {
    fun toNode(trace: Trace): Node {
        return OpNode(trace, this)
    }

}
