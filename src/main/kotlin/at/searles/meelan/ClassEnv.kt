package at.searles.meelan

class ClassEnv(val decl: ClassDecl, val table: SymbolTable) : Node(decl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
