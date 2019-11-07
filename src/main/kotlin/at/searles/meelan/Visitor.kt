package at.searles.meelan

interface Visitor<T> {
    fun visit(app: App): T
    fun visit(block: Block): T
    fun visit(classDecl: ClassDecl): T
    fun visit(defDecl: DefDecl): T
    fun visit(forStmt: For): T
    fun visit(frame: Frame): T
    fun visit(funDecl: FunDecl): T
    fun visit(idNode: IdNode): T
    fun visit(ifStmt: If): T
    fun visit(ifElse: IfElse): T
    fun visit(intNode: IntNode): T
    fun visit(qualifiedNode: QualifiedNode): T
    fun visit(realNode: RealNode): T
    fun visit(stringNode: StringNode): T
    fun visit(varDecl: VarDecl): T
    fun visit(varParameter: VarParameter): T
    fun visit(vectorNode: VectorNode): T
    fun visit(whileStmt: While): T
}