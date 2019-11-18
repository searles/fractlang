package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.parsing.Trace
class IntNode(trace: Trace, val value: Int) : Node(trace), ConstValue {
    init {
        type = BaseTypes.Int
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun isZero(): Boolean {
        return value == 0
    }

    override fun isOne(): Boolean {
        return value == 1
    }
}
