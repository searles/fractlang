package at.searles.fractlang.linear

import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.Visitor
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Assign
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Jump
import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmInstruction

class LinearizeExpr(private val code: LinearizedCode, private val varNameGenerator: Iterator<String>, private val optTargetNode: IdNode?): Visitor<VmArg> {

    override fun visit(app: App): VmArg {
        val op = (app.head as OpNode).op as BaseOp

        val index = op.getArgKindOffset(app.args)

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
        require(block.stmts.isNotEmpty())
        if(block.stmts.isEmpty()) {
            // FIXME shouldn't this be caught before?
            throw SemanticAnalysisException(
                "not an expression",
                block.trace
            )
        }

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
            code.addInstruction(
                VmInstruction(
                    Assign,
                    Assign.getArgKindOffset(args as List<Node>),
                    args
                )
            )
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
        error("not applicable")
    }

    override fun visit(varDecl: VarDecl): VmArg {
        error("not applicable")
    }

    override fun visit(forStmt: For): VmArg {
        error("not applicable")
    }

    override fun visit(ifStmt: If): VmArg {
        error("not applicable")
    }

    override fun visit(whileStmt: While): VmArg {
        error("not applicable") // should be a type error.
    }

    override fun visit(classEnv: ClassEnv): VmArg {
        error("not applicable")
    }

    override fun visit(funEnv: FunEnv): VmArg {
        error("not applicable")
    }

    override fun visit(opNode: OpNode): VmArg {
        error("not applicable")
    }

    override fun visit(objectNode: ObjectNode): VmArg {
        error("not applicable")
    }

    override fun visit(valDecl: ValDecl): VmArg {
        error("not applicable")
    }

    override fun visit(nop: Nop): VmArg {
        error("not applicable")
    }

    override fun visit(classDecl: ClassDecl): VmArg {
        error("not applicable")
    }

    override fun visit(funDecl: FunDecl): VmArg {
        error("not applicable")
    }

    override fun visit(qualifiedNode: QualifiedNode): VmArg {
        error("not applicable")
    }

    override fun visit(stringNode: StringNode): VmArg {
        error("not applicable")
    }

    override fun visit(varParameter: VarParameter): VmArg {
        error("not applicable")
    }

    override fun visit(vectorNode: VectorNode): VmArg {
        error("not applicable")
    }

    override fun visit(assignment: Assignment): VmArg {
        error("not applicable")
    }

    override fun visit(externDecl: ExternDecl): VmArg {
        error("not applicable")
    }

    override fun visit(externNode: ExternNode): VmArg {
        error("not applicable")
    }
}
