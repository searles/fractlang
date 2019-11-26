package at.searles.fractlang.linear

import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.Visitor
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Jump
import at.searles.fractlang.vm.VmInstruction

class LinearizeStmt(val code: LinearCode, val varNameGenerator: Iterator<String>): Visitor<Unit> {

    override fun visit(app: App) {
        // FIXME Are there any?
        error("test")
        require(app.head is OpNode && app.head.op is BaseOp)

        val op: BaseOp = app.head.op

        val linearizedArgs = app.args.map { it.accept(LinearizeExpr(code, varNameGenerator, null))}
        code.addInstruction(
            VmInstruction(
                op,
                op.getArgKindOffset(app.args),
                linearizedArgs
            )
        )
    }

    override fun visit(assignment: Assignment) {
        require(assignment.lhs is IdNode)
        assignment.rhs.accept(LinearizeExpr(code, varNameGenerator, assignment.lhs))
    }

    override fun visit(block: Block) {
        block.stmts.forEach {
            it.accept(this)
        }
    }

    override fun visit(varDecl: VarDecl) {
        require(varDecl.init == null && varDecl.varType != null)

        if(varDecl.varType.vmCodeSize() == 0) {
            throw SemanticAnalysisException(
                "not an assignable expression",
                varDecl.trace
            )
        }

        code.alloc(Alloc(varDecl.name, varDecl.varType!!))
    }

    override fun visit(idNode: IdNode) {
        throw SemanticAnalysisException(
            "not a stmt",
            idNode.trace
        )
    }

    override fun visit(forStmt: For) {
        throw SemanticAnalysisException(
            "for not (yet) supported",
            forStmt.trace
        )
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
        error("not applicable")
    }

    override fun visit(realNode: RealNode) {
        error("not applicable")
    }

    override fun visit(vectorNode: VectorNode) {
        error("not applicable")
    }

    override fun visit(cplxNode: CplxNode) {
        error("not applicable")
    }

    override fun visit(boolNode: BoolNode) {
        error("not applicable")
    }

    override fun visit(opNode: OpNode) {
        error("not applicable")
    }

    override fun visit(stringNode: StringNode) {
        error("not applicable")
    }

    override fun visit(classDecl: ClassDecl) {
        error("not applicable")
    }

    override fun visit(funDecl: FunDecl) {
        error("not applicable")
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        error("not applicable")
    }

    override fun visit(varParameter: VarParameter) {
        error("not applicable")
    }

    override fun visit(valDecl: ValDecl) {
        error("not applicable")
    }

    override fun visit(nop: Nop) {
        error("not applicable")
    }

    override fun visit(objectNode: ObjectNode) {
        error("not applicable")
    }
    override fun visit(classEnv: ClassEnv) {
        error("not applicable")
    }

    override fun visit(funEnv: FunEnv) {
        error("not applicable")
    }

}