package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.Type
import at.searles.meelan.Visitor
import at.searles.parsing.Trace
class VarDecl(trace: Trace, val name: String, val varType: Type?, val init: Node?): Node(trace) {

    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}