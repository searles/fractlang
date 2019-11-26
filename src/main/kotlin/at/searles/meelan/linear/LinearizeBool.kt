package at.searles.meelan.linear

import at.searles.meelan.Visitor
import at.searles.meelan.nodes.*
import at.searles.meelan.ops.*
import java.lang.IllegalArgumentException

class LinearizeBool(val code: LinearCode, val varNameGenerator: Iterator<String>, val trueLabel: Label, val falseLabel: Label): Visitor<Unit> {
    override fun visit(boolNode: BoolNode) {
        throw IllegalArgumentException("should have been inlined")
    }

    private fun visitAnd(args: List<Node>) {
        val midLabel = Label()
        args[0].accept(LinearizeBool(code, varNameGenerator, midLabel, falseLabel))
        code.addLabel(midLabel)
        args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
    }

    private fun visitOr(args: List<Node>) {
        val midLabel = Label()
        args[0].accept(LinearizeBool(code, varNameGenerator, trueLabel, midLabel))
        code.addLabel(midLabel)
        args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
    }

    private fun visitXor(args: List<Node>) {
        val midTrueLabel = Label()
        val midFalseLabel = Label()
        args[0].accept(LinearizeBool(code, varNameGenerator, midTrueLabel, midFalseLabel))
        code.addLabel(midTrueLabel)
        args[1].accept(LinearizeBool(code, varNameGenerator, falseLabel, trueLabel))
        code.addLabel(midFalseLabel)
        args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
    }

    private fun visitNot(args: List<Node>) {
        args[0].accept(LinearizeBool(code, varNameGenerator, falseLabel, trueLabel))
    }

    private fun visitExpr(op: BaseOp, args: List<Node>) {
        val index = op.indexOfParameterConfiguration(args)
        val linearizedArgs = args.map {
            it.accept(LinearizeExpr(code, varNameGenerator, null))
        }

        code.addInstruction(VmInstruction(op, index, linearizedArgs + trueLabel + falseLabel))
    }

    override fun visit(app: App) {
        require(app.head is OpNode && app.head.op is BaseOp)

        when(app.head.op) {
            is And -> visitAnd(app.args)
            is Or -> visitOr(app.args)
            is Xor -> visitXor(app.args)
            is Not -> visitNot(app.args)
            else -> visitExpr(app.head.op as BaseOp, app.args)
        }
    }

    override fun visit(block: Block) {
        block.stmts.dropLast(1).forEach {
            it.accept(LinearizeStmt(code, varNameGenerator))
        }

        return block.stmts.last().accept(this)
    }

    override fun visit(ifElse: IfElse) {
        val condTrueLabel = Label()
        val condFalseLabel = Label()

        ifElse.condition.accept(LinearizeBool(code, varNameGenerator, condTrueLabel, condFalseLabel))
        code.addLabel(condTrueLabel)
        ifElse.thenBranch.accept(this)
        code.addLabel(condFalseLabel)
        ifElse.elseBranch.accept(this)
    }

    override fun visit(classDecl: ClassDecl) {
        error("not applicable")
    }

    override fun visit(forStmt: For) {
        error("not applicable")
    }

    override fun visit(funDecl: FunDecl) {
        error("not applicable")
    }

    override fun visit(idNode: IdNode) {
        error("not applicable")
    }

    override fun visit(ifStmt: If) {
        error("not applicable")
    }

    override fun visit(intNode: IntNode) {
        error("not applicable")
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        error("not applicable")
    }

    override fun visit(realNode: RealNode) {
        error("not applicable")
    }

    override fun visit(stringNode: StringNode) {
        error("not applicable")
    }

    override fun visit(varDecl: VarDecl) {
        error("not applicable")
    }

    override fun visit(varParameter: VarParameter) {
        error("not applicable")
    }

    override fun visit(vectorNode: VectorNode) {
        error("not applicable")
    }

    override fun visit(whileStmt: While) {
        error("not applicable")
    }

    override fun visit(classEnv: ClassEnv) {
        error("not applicable")
    }

    override fun visit(funEnv: FunEnv) {
        error("not applicable")
    }

    override fun visit(cplxNode: CplxNode) {
        error("not applicable")
    }

    override fun visit(nop: Nop) {
        error("not applicable")
    }

    override fun visit(opNode: OpNode) {
        error("not applicable")
    }

    override fun visit(objectNode: ObjectNode) {
        error("not applicable")
    }

    override fun visit(valDecl: ValDecl) {
        error("not applicable")
    }

    override fun visit(assignment: Assignment) {
        error("not applicable")
    }
}
