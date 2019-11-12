package at.searles.meelan

class FunEnv(funDecl: FunDecl, context: Frame) : Node(funDecl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
