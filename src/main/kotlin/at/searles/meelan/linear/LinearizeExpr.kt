package at.searles.meelan.linear

import at.searles.meelan.Visitor
import at.searles.meelan.nodes.*
import at.searles.meelan.ops.Assign
import at.searles.meelan.ops.BaseOp
import java.lang.IllegalArgumentException

class LinearizeExpr(val code: LinearCode, val varNameGenerator: Iterator<String>, val optTargetNode: IdNode?): Visitor<VmArg> {

    private fun assignToTargetNode(expr: Node): VmArg {
        val targetNode = optTargetNode ?: run {
            val resultVarName = varNameGenerator.next()
            IdNode(expr.trace, resultVarName)
        }

        return targetNode
    }

    override fun visit(app: App): VmArg {
        val op = (app.head as OpNode).op as BaseOp

        val index = op.indexOf(app.args)

        val linearizedArgs = app.args.map { it.accept(
            LinearizeExpr(
                code,
                varNameGenerator,
                null
            )
        )}

        val target = optTargetNode ?: IdNode(app.trace, varNameGenerator.next()).apply { type = app.type }

        // last argument is the target.
        code.addInstruction(VmInstruction(op, index, linearizedArgs + target))

        if(optTargetNode == null) {
            code.alloc(Alloc(target.id, target.type))
        }

        return target
    }

    override fun visit(block: Block): VmArg {
        block.stmts.dropLast(1).forEach {
            it.accept(LinearizeStmt(code, varNameGenerator))
        }

        return block.stmts.last().accept(this)
    }

    private fun assignToOptTargetNode() {
        // TODO
    }

    override fun visit(idNode: IdNode): VmArg {
        // FIXME must assign
        if(optTargetNode != null) {
            val args =  listOf(optTargetNode, idNode)
            code.addInstruction(VmInstruction(Assign, Assign.indexOf(args), args))
            return optTargetNode
        }

        return idNode
    }

    override fun visit(intNode: IntNode): VmArg {
        return intNode
    }

    override fun visit(realNode: RealNode): VmArg {
        return realNode
    }

    override fun visit(cplxNode: CplxNode): VmArg {
        return cplxNode
    }

    override fun visit(boolNode: BoolNode): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(ifElse: IfElse): VmArg {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varDecl: VarDecl): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(forStmt: For): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(ifStmt: If): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(whileStmt: While): VmArg {
        throw IllegalArgumentException() // should be a type error.
    }

    override fun visit(classEnv: ClassEnv): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(funEnv: FunEnv): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(opNode: OpNode): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(objectNode: ObjectNode): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(valDecl: ValDecl): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(nop: Nop): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(classDecl: ClassDecl): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(funDecl: FunDecl): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(qualifiedNode: QualifiedNode): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(stringNode: StringNode): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(varParameter: VarParameter): VmArg {
        throw IllegalArgumentException()
    }

    override fun visit(vectorNode: VectorNode): VmArg {
        throw IllegalArgumentException()
    }
}
