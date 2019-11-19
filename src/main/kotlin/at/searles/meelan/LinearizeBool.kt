package at.searles.meelan

import at.searles.meelan.nodes.*

class LinearizeBool(val stmts: ArrayList<Node> = ArrayList(), val varNameGenerator: Iterator<String>, trueLabel: Node, falseLabel: Node): Visitor<Unit> {
    override fun visit(boolNode: BoolNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(block: Block) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(classDecl: ClassDecl) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(forStmt: For) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funDecl: FunDecl) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(idNode: IdNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifStmt: If) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifElse: IfElse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(intNode: IntNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(realNode: RealNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(stringNode: StringNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varDecl: VarDecl) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varParameter: VarParameter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(vectorNode: VectorNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(whileStmt: While) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(classEnv: ClassEnv) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funEnv: FunEnv) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(cplxNode: CplxNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(nop: Nop) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(opNode: OpNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(objectNode: ObjectNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(valDecl: ValDecl) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
