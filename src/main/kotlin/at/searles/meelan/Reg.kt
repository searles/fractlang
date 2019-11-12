package at.searles.meelan

import at.searles.parsing.Trace

class Reg(trace: Trace, type: Type, val relativeOffset: Int): Node(trace) {
    init {
        this.type = type
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
