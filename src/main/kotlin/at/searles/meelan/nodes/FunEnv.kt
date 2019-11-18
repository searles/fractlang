package at.searles.meelan.nodes

import at.searles.meelan.BaseTypes
import at.searles.meelan.SymbolTable
import at.searles.meelan.Visitor

class FunEnv(val decl: FunDecl, val table: SymbolTable) : Node(decl.trace) {

    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
