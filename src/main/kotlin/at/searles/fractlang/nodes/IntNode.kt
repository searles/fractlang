package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmCode
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
