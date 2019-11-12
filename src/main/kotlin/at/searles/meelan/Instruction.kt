package at.searles.meelan

import at.searles.meelan.ops.BaseOp
import at.searles.parsing.Trace


class Instruction(trace: Trace, val op: BaseOp, vararg val arguments: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
