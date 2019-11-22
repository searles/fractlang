package at.searles.meelan.linear

import at.searles.meelan.Visitor
import at.searles.meelan.nodes.*
import at.searles.meelan.ops.Assign
import at.searles.meelan.ops.BaseOp
import at.searles.meelan.ops.Jump
import java.lang.IllegalArgumentException

class LinearizeExpr(private val code: LinearCode, private val varNameGenerator: Iterator<String>, private val optTargetNode: IdNode?): Visitor<VmArg> {

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

    override fun visit(ifElse: IfElse): VmArg {
        val targetNode = optTargetNode ?: IdNode(ifElse.trace, varNameGenerator.next()).apply { type = ifElse.type }

        // last argument is the target.
        val trueLabel = Label()
        val falseLabel = Label()
        val endLabel = Label()

        ifElse.condition.accept(LinearizeBool(code, varNameGenerator, trueLabel, falseLabel))
        code.addLabel(trueLabel)
        ifElse.thenBranch.accept(LinearizeExpr(code, varNameGenerator, targetNode))
        code.addInstruction(VmInstruction(Jump, 0, listOf(endLabel)))
        code.addLabel(falseLabel)
        ifElse.elseBranch.accept(LinearizeExpr(code, varNameGenerator, targetNode))
        code.addLabel(endLabel)

        if(optTargetNode == null) {
            code.alloc(Alloc(targetNode.id, targetNode.type))
        }

        return targetNode
    }

    override fun visit(block: Block): VmArg {
        block.stmts.dropLast(1).forEach {
            it.accept(LinearizeStmt(code, varNameGenerator))
        }

        return block.stmts.last().accept(this)
    }

    override fun visit(idNode: IdNode): VmArg {
        if(optTargetNode != null && optTargetNode.id != idNode.id) {
            return assignIfTargetNode(idNode)
        }

        return idNode
    }

    private fun assignIfTargetNode(arg: VmArg): VmArg {
        if(optTargetNode != null) {
            val args =  listOf(optTargetNode, arg)
            @Suppress("UNCHECKED_CAST")
            code.addInstruction(VmInstruction(Assign, Assign.indexOf(args as List<Node>), args))
            return optTargetNode
        }

        return arg
    }

    override fun visit(intNode: IntNode): VmArg {
        return assignIfTargetNode(intNode)
    }

    override fun visit(realNode: RealNode): VmArg {
        return assignIfTargetNode(realNode)
    }

    override fun visit(cplxNode: CplxNode): VmArg {
        return assignIfTargetNode(cplxNode)
    }

    override fun visit(boolNode: BoolNode): VmArg {
        throw IllegalArgumentException()
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