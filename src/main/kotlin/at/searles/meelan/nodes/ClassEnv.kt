package at.searles.meelan.nodes

import at.searles.meelan.SymbolTable
import at.searles.meelan.Visitor

class ClassEnv(val decl: ClassDecl, val table: SymbolTable) : Node(decl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
