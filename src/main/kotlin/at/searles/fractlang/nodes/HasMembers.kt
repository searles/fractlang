package at.searles.fractlang.nodes

import at.searles.parsing.Trace

interface HasMembers {
    fun getMember(trace: Trace, memberId: String): Node
}
