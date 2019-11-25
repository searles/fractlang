package at.searles.meelan.linear

import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.Visitor
import at.searles.meelan.nodes.*
import at.searles.meelan.ops.BaseOp
import at.searles.meelan.ops.Jump

class LinearizeStmt(val code: LinearCode, val varNameGenerator: Iterator<String>): Visitor<Unit> {

    override fun visit(app: App) {
        require(app.head is OpNode && app.head.op is BaseOp)

        val op: BaseOp = app.head.op

        val linearizedArgs = app.args.map { it.accept(LinearizeExpr(code, varNameGenerator, null))}
        code.addInstruction(VmInstruction(op, op.indexOfParameterConfiguration(app.args), linearizedArgs))
    }

    override fun visit(block: Block) {
        block.stmts.forEach {
            it.accept(this)
        }
    }

    override fun visit(varDecl: VarDecl) {
        require(varDecl.init == null)
        code.alloc(Alloc(varDecl.name, varDecl.type))
    }

    override fun visit(idNode: IdNode) {
        throw SemanticAnalysisException("not a stmt", idNode.trace)
    }

    override fun visit(forStmt: For) {
        throw SemanticAnalysisException("for not (yet) supported", forStmt.trace)
    }

    override fun visit(ifStmt: If) {
        val trueLabel = Label()
        val falseLabel = Label()

        ifStmt.condition.accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
        code.addLabel(trueLabel)
        ifStmt.thenBranch.accept(this)
        code.addLabel(falseLabel)
    }

    override fun visit(ifElse: IfElse) {
        val trueLabel = Label()
        val falseLabel = Label()
        val endLabel = Label()

        ifElse.condition.accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
        code.addLabel(trueLabel)
        ifElse.thenBranch.accept(this)
        code.addInstruction(VmInstruction(Jump, 0, listOf(endLabel)))
        code.addLabel(falseLabel)
        ifElse.elseBranch.accept(this)
        code.addLabel(endLabel)
    }

    override fun visit(whileStmt: While) {
        val startLabel = Label()
        val trueLabel = Label()
        val falseLabel = Label()

        code.addLabel(startLabel)
        whileStmt.condition.accept(
            LinearizeBool(
                code,
                varNameGenerator,
                trueLabel,
                falseLabel
            )
        )
        code.addLabel(trueLabel)
        whileStmt.body.accept(this)
        code.addInstruction(VmInstruction(Jump, 0, listOf(startLabel)))
        code.addLabel(falseLabel)
    }

    override fun visit(intNode: IntNode) {
        require(false)
    }

    override fun visit(realNode: RealNode) {
        require(false)
    }

    override fun visit(vectorNode: VectorNode) {
        require(false)
    }

    override fun visit(cplxNode: CplxNode) {
        require(false)
    }

    override fun visit(boolNode: BoolNode) {
        require(false)
    }

    override fun visit(opNode: OpNode) {
        require(false)
    }

    override fun visit(stringNode: StringNode) {
        require(false)
    }

    override fun visit(classDecl: ClassDecl) {
        require(false)
    }

    override fun visit(funDecl: FunDecl) {
        require(false)
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        require(false)
    }

    override fun visit(varParameter: VarParameter) {
        require(false)
    }

    override fun visit(valDecl: ValDecl) {
        require(false)
    }

    override fun visit(nop: Nop) {
        require(false)
    }

    override fun visit(objectNode: ObjectNode) {
        require(false)
    }
    override fun visit(classEnv: ClassEnv) {
        require(false)
    }

    override fun visit(funEnv: FunEnv) {
        require(false)
    }
}