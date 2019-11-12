package at.searles.meelan

import at.searles.parsing.Trace
class RealNode(trace: Trace, val value: Double) : Node(trace) {
    init {
        type = BaseTypes.Real
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
