package at.searles.meelan.nodes

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.meelan.linear.VmArg
import at.searles.meelan.linear.VmCode
import at.searles.parsing.Trace

class CplxNode(trace: Trace, val value: Cplx) : Node(trace), NumValue, VmArg.Num {
    init {
        type = BaseTypes.Cplx
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun addToVmCode(vmCode: VmCode) {
        vmCode.add(value)
    }

    override fun isZero(): Boolean {
        return value.re() == 0.0 && value.im() == 0.0
    }

    override fun isOne(): Boolean {
        return value.re() == 1.0 && value.im() == 0.0
    }

    override fun toString(): String {
        return "$value"
    }
}
