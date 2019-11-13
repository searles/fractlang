package at.searles.meelan

class ClassEnv(decl: ClassDecl, context: SymbolTable) : Node(decl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
