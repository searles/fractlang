package at.searles.meelan.nodes

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.Visitor
import at.searles.meelan.linear.VmArg
import at.searles.meelan.linear.VmCode
import at.searles.parsing.Trace
class CplxNode(trace: Trace, val value: Cplx) : Node(trace), VmArg {
    init {
        type = BaseTypes.Cplx
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun addToVmCode(vmCode: VmCode) {
        // TODO Move to commons
        val l = java.lang.Double.doubleToRawLongBits(d)
        // beware of big endian systems [are there any?]
        return intArrayOf((l and 0x0ffffffffL).toInt(), (l shr 32).toInt())
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
