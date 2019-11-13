package at.searles.meelan

class FunEnv(val decl: FunDecl, val table: SymbolTable) : Node(decl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
