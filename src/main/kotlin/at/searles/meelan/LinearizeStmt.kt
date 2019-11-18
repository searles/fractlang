package at.searles.meelan

import at.searles.meelan.nodes.*

class LinearizeStmt(val stmts: ArrayList<Node> = ArrayList(), val varNameGenerator: Iterator<String>): Visitor<Unit> {

    override fun visit(app: App) {
        val linearizedArgs = app.args.map { it.accept(LinearizeExpr(stmts, varNameGenerator))}
        stmts.add(App(app.trace, app.head, linearizedArgs))
    }

    override fun visit(block: Block) {
        block.stmts.forEach {
            it.accept(this)
        }
    }

    override fun visit(varDecl: VarDecl) {
        require(varDecl.init == null) {"must inline first"}
        stmts.add(varDecl)
    }

    override fun visit(idNode: IdNode) {
        throw SemanticAnalysisException("not a stmt", idNode.trace)
    }

    override fun visit(forStmt: For) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifStmt: If) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifElse: IfElse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(intNode: IntNode) {
        require(false) {"must inline first"}
    }

    override fun visit(realNode: RealNode) {
        require(false) {"must inline first"}
    }

    override fun visit(vectorNode: VectorNode) {
        require(false) {"must inline first"}
    }

    override fun visit(whileStmt: While) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(cplxNode: CplxNode) {
        require(false) {"must inline first"}
    }

    override fun visit(boolNode: BoolNode) {
        require(false) {"must inline first"}
    }

    override fun visit(opNode: OpNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(stringNode: StringNode) {
        require(false) {"must inline first"}
    }

    override fun visit(classDecl: ClassDecl) {
        require(false) {"must inline first"}
    }

    override fun visit(funDecl: FunDecl) {
        require(false) {"must inline first"}
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        require(false) {"must inline first"}
    }

    override fun visit(varParameter: VarParameter) {
        require(false) {"must inline first"}
    }

    override fun visit(valDecl: ValDecl) {
        require(false) {"must inline first"}
    }

    override fun visit(nop: Nop) {
        require(false) {"must inline first"}
    }

    override fun visit(objectNode: ObjectNode) {
        require(false) {"must inline first"}
    }
    override fun visit(classEnv: ClassEnv) {
        require(false) {"must inline first"}
    }

    override fun visit(funEnv: FunEnv) {
        require(false) {"must inline first"}
    }
}