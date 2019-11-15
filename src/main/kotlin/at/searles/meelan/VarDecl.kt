package at.searles.meelan

import at.searles.parsing.Trace
class VarDecl(trace: Trace, val name: String, val varType: Type?, val init: Node?): Node(trace) {

    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}