package at.searles.meelan

class ClassEnv(classDecl: ClassDecl, context: Frame) : Node(classDecl.trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
