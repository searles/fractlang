package at.searles.meelan

import at.searles.meelan.nodes.*
import at.searles.meelan.ops.Jump
import at.searles.parsing.Trace

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
        // TODO
        throw SemanticAnalysisException("for not (yet) supported", forStmt.trace)
    }

    private fun createLabel(trace: Trace): Node {
        // FIXME idNode is nice for output but how to ensure that it is properly handled?
        // Examples are Generation of VM.
        return IdNode(trace, varNameGenerator.next()).apply { type = BaseTypes.Unit }
    }

    override fun visit(ifStmt: If) {
        val trueLabel = createLabel(ifStmt.condition.trace)
        val falseLabel = createLabel(ifStmt.condition.trace)

        ifStmt.condition.accept(LinearizeBool(stmts, varNameGenerator, trueLabel, falseLabel))
        stmts.add(trueLabel)
        ifStmt.thenBranch.accept(this)
        stmts.add(falseLabel)
    }

    override fun visit(ifElse: IfElse) {
        val trueLabel = createLabel(ifElse.condition.trace)
        val falseLabel = createLabel(ifElse.condition.trace)
        val endLabel = createLabel(ifElse.condition.trace)

        ifElse.condition.accept(LinearizeBool(stmts, varNameGenerator, trueLabel, falseLabel))
        stmts.add(trueLabel)
        ifElse.thenBranch.accept(this)
        stmts.add(Jump.apply(ifElse.trace, listOf(endLabel)))
        stmts.add(falseLabel)
        ifElse.elseBranch.accept(this)
        stmts.add(endLabel)
    }

    override fun visit(whileStmt: While) {
        val startLabel = createLabel(whileStmt.trace)
        val trueLabel = createLabel(whileStmt.condition.trace)
        val falseLabel = createLabel(whileStmt.condition.trace)

        stmts.add(startLabel)
        whileStmt.condition.accept(LinearizeBool(stmts, varNameGenerator, trueLabel, falseLabel))
        stmts.add(trueLabel)
        whileStmt.body.accept(this)
        stmts.add(Jump.apply(whileStmt.trace, listOf(startLabel)))
        stmts.add(falseLabel)
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

    override fun visit(cplxNode: CplxNode) {
        require(false) {"must inline first"}
    }

    override fun visit(boolNode: BoolNode) {
        require(false) {"must inline first"}
    }

    override fun visit(opNode: OpNode) {
        require(false) {"must inline first"}
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