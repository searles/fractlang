package at.searles.meelan

import at.searles.meelan.ops.Assign
import at.searles.parsing.Trace

class InlineAppVisitor(val trace: Trace, val args: List<Node>, val parentVisitor: InlineVisitor): Visitor<Node> {
    private fun defineArgs(parameters: List<Node>, innerVisitor: InlineVisitor) {
        parameters.zip(args).forEach {
            // TODO: if it is a var add assignment add it to inner block
            if(it.first is IdNode) {
            } else if(it.first is VarParameter) {
            } else {
                throw IllegalArgumentException("parameters must be Id or Var!")
            }

            innerVisitor.block.add(App(it.second.trace, Assign, listOf(paramId, args)))


        }
    }

    override fun visit(funEnv: FunEnv): Node {
        if(funEnv.decl.parameters.size != args.size) {
            throw SemanticAnalysisException("bad number of arguments", trace)
        }

        val innerVisitor = InlineVisitor(funEnv.table, parentVisitor.varNameGenerator)

        defineArgs(funEnv.decl.parameters, innerVisitor)

        val returnValue = funEnv.decl.accept(innerTable, innerBlock))

        block.add(innerBlock)

        return returnValue
    }

    override fun visit(classEnv: ClassEnv): Node {
        val innerTable = classEnv.table.spawnChildTable()
        val innerBlock = ArrayList<Node>()
        // TODO check arity
        defineArgs(classEnv.decl.parameters, innerTable)

        classDecl.block.accept(InlineVisitor(innerTable, innerBlock))

        block.add(Block(innerBlock))

        return ObjectNode(objectTable)
    }

    override fun visit(op: Op): Node {
        // inline non-base-ops?
        // yes, why not. Types are also fine.
        return op.apply()
    }

    override fun visit(app: App): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(block: Block): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(classDecl: ClassDecl): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(forStmt: For): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funDecl: FunDecl): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(idNode: IdNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifStmt: If): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifElse: IfElse): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(intNode: IntNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(realNode: RealNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(stringNode: StringNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varDecl: VarDecl): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varParameter: VarParameter): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(vectorNode: VectorNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(whileStmt: While): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(cplxNode: CplxNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(boolNode: BoolNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(nop: Nop): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
