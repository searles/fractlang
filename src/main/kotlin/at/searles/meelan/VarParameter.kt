package at.searles.meelan

import at.searles.parsing.Trace
class VarParameter(trace: Trace,  val name: String, val varType: Type?): Node(trace) {

    init {
        if(varType != null) {
            type = varType
        }
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}