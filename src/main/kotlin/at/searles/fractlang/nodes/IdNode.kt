package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmCode
import at.searles.parsing.Trace
class IdNode(trace: Trace, val id: String): Node(trace), VmArg {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun vmCodeSize(): Int {
        return 1
    }

    override fun addToVmCode(vmCode: VmCode) {
        vmCode.add(id)
    }

    override fun toString(): String {
        return id
    }
}