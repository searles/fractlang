package at.searles.meelan

import at.searles.commons.math.Cplx
import at.searles.parsing.Trace
class CplxNode(trace: Trace, val value: Cplx) : Node(trace) {

    init {
        type = BaseTypes.Cplx
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
