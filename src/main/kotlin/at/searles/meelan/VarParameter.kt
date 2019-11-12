package at.searles.meelan

import at.searles.parsing.Trace
class VarParameter(trace: Trace,  val name: String, val typeName: String?): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}