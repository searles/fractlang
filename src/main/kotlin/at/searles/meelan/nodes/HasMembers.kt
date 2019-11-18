package at.searles.meelan.nodes

import at.searles.parsing.Trace

interface HasMembers {
    fun getMember(trace: Trace, memberId: String): Node
}
