package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.meelan.linear.VmArg
import at.searles.meelan.linear.VmCode
import at.searles.parsing.Trace

class RealNode(trace: Trace, val value: Double) : Node(trace), ConstValue, VmArg.Num {
    init {
        type = BaseTypes.Real
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun addToVmCode(vmCode: VmCode) {
        vmCode.add(value)
    }

    override fun isZero(): Boolean {
        return value == 0.0
    }

    override fun isOne(): Boolean {
        return value == 1.0
    }

    override fun toString(): String {
        return "$value"
    }
}
