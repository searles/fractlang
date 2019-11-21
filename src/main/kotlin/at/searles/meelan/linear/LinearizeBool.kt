package at.searles.meelan.linear

import at.searles.meelan.Visitor
import at.searles.meelan.nodes.*
import at.searles.meelan.ops.*
import java.lang.IllegalArgumentException

class LinearizeBool(val code: LinearCode, val varNameGenerator: Iterator<String>, val trueLabel: Label, val falseLabel: Label): Visitor<Unit> {
    override fun visit(boolNode: BoolNode) {
        throw IllegalArgumentException("should have been inlined")
    }

    override fun visit(app: App) {
        require(app.head is OpNode)

        when(app.head.op) {
            is And -> {
                val midLabel = Label()
                app.args[0].accept(LinearizeBool(code, varNameGenerator, midLabel, falseLabel))
                code.addLabel(midLabel)
                app.args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
            }
            is Or -> {
                val midLabel = Label()
                app.args[0].accept(LinearizeBool(code, varNameGenerator, trueLabel, midLabel))
                code.addLabel(midLabel)
                app.args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
            }
            is Xor -> {
                val midTrueLabel = Label()
                val midFalseLabel = Label()
                app.args[0].accept(LinearizeBool(code, varNameGenerator, midTrueLabel, midFalseLabel))
                code.addLabel(midTrueLabel)
                app.args[1].accept(LinearizeBool(code, varNameGenerator, falseLabel, trueLabel))
                code.addLabel(midFalseLabel)
                app.args[1].accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
            }
            is Not -> app.args[0].accept(LinearizeBool(code, varNameGenerator, falseLabel, trueLabel))
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
        require(false)
    }

    override fun visit(forStmt: For) {
        require(false)
    }

    override fun visit(funDecl: FunDecl) {
        require(false)
    }

    override fun visit(idNode: IdNode) {
        require(false)
    }

    override fun visit(ifStmt: If) {
        require(false)
    }

    override fun visit(intNode: IntNode) {
        require(false)
    }

    override fun visit(qualifiedNode: QualifiedNode) {
        require(false)
    }

    override fun visit(realNode: RealNode) {
        require(false)
    }

    override fun visit(stringNode: StringNode) {
        require(false)
    }

    override fun visit(varDecl: VarDecl) {
        require(false)
    }

    override fun visit(varParameter: VarParameter) {
        require(false)
    }

    override fun visit(vectorNode: VectorNode) {
        require(false)
    }

    override fun visit(whileStmt: While) {
        require(false)
    }

    override fun visit(classEnv: ClassEnv) {
        require(false)
    }

    override fun visit(funEnv: FunEnv) {
        require(false)
    }

    override fun visit(cplxNode: CplxNode) {
        require(false)
    }

    override fun visit(nop: Nop) {
        require(false)
    }

    override fun visit(opNode: OpNode) {
        require(false)
    }

    override fun visit(objectNode: ObjectNode) {
        require(false)
    }

    override fun visit(valDecl: ValDecl) {
        require(false)
    }
}
