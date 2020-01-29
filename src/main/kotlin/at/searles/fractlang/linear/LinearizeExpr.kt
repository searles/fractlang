package at.searles.fractlang.linear

import at.searles.fractlang.Visitor
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Assign
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Jump
import at.searles.fractlang.ops.VmBaseOp
import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmInstruction

class LinearizeExpr(private val code: ArrayList<CodeLine>, private val nameGenerator: Iterator<String>, private val optTargetNode: IdNode?): Visitor<VmArg> {

    override fun visit(app: App): VmArg {
        val op = (app.head as OpNode).op as VmBaseOp

        val index = op.getArgKindOffset(app.args)

        val linearizedArgs = app.args.map { it.accept(
            LinearizeExpr(
                code,
                nameGenerator,
                null
            )
        )}

        val target = optTargetNode ?: IdNode(app.trace, nameGenerator.next()).apply { type = app.type }

        // last argument is the target.
        code.add(VmInstruction(op, index, linearizedArgs + target))

        if(optTargetNode == null) {
            code.add(Alloc(target.id, target.type))
        }

        return target
    }

    override fun visit(ifElse: IfElse): VmArg {
        val targetNode = optTargetNode ?: IdNode(ifElse.trace, nameGenerator.next()).apply { type = ifElse.type }

        // last argument is the target.
        val trueLabel = Label(nameGenerator.next())
        val falseLabel = Label(nameGenerator.next())
        val endLabel = Label(nameGenerator.next())

        ifElse.condition.accept(LinearizeBool(code, nameGenerator, trueLabel, falseLabel))
        code.add(trueLabel)
        ifElse.thenBranch.accept(LinearizeExpr(code, nameGenerator, targetNode))
        code.add(VmInstruction(Jump, 0, listOf(endLabel)))
        code.add(falseLabel)
        ifElse.elseBranch.accept(LinearizeExpr(code, nameGenerator, targetNode))
        code.add(endLabel)

        if(optTargetNode == null) {
            code.add(Alloc(targetNode.id, targetNode.type))
        }

        return targetNode
    }

    override fun visit(block: Block): VmArg {
        require(block.stmts.isNotEmpty())

        block.stmts.dropLast(1).forEach {
            it.accept(LinearizeStmt(code, nameGenerator))
        }

        code.add(VarBound(block.stmts.dropLast(1).filterIsInstance<VarDecl>().map {
            IdNode(it.trace, it.name).apply { type = it.varType!! }
        }))

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
            require(optTargetNode.type == (arg as Node).type)

            val convArg = optTargetNode.type.convert(arg as Node)
            val args =  listOf(optTargetNode, convArg)

            require(convArg is VmArg)

            @Suppress("UNCHECKED_CAST")
            code.add(
                VmInstruction(
                    Assign,
                    Assign.getArgKindOffset(args),
                    args as List<VmArg>
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

    override fun visit(opNode: OpNode): VmArg {
        return visit(App(opNode.trace, opNode, emptyList()).apply { type = (opNode.op as BaseOp).signatures[0].returnType })
    }

    override fun visit(qualifiedNode: QualifiedNode): VmArg {
        error("not applicable")
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

    override fun visit(indexedNode: IndexedNode): VmArg {
        error("not applicable")
    }
}
