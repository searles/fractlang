package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.meelan.linear.VmArg
import at.searles.meelan.linear.VmCode
import at.searles.parsing.Trace

class IntNode(trace: Trace, val value: Int) : Node(trace), NumValue, VmArg.Num {
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

    override fun addToVmCode(vmCode: VmCode) {
        vmCode.add(value)
    }

    override fun toString(): String {
        return "$value"
    }
}
