package at.searles.meelan

import at.searles.meelan.ops.Assign

class InlineVisitor(val parentTable: SymbolTable, val varNameGenerator: Iterator<String>): Visitor<Node> {
    val block = ArrayList<Node>()
    val table = parentTable.fork()

    override fun visit(funDecl: FunDecl): Node {
        if(!table.set(funDecl.name, FunEnv(funDecl, table))) {
            throw SemanticAnalysisException("already defined", funDecl)
        }

        return Nop(funDecl.trace)
    }

    override fun visit(classDecl: ClassDecl): Node {
        if(!table.set(classDecl.name, ClassEnv(classDecl, table))) {
            throw SemanticAnalysisException("already defined", classDecl)
        }

        return Nop(classDecl.trace)
    }

    override fun visit(app: App): Node {
        val args = app.args.map { it.accept(this) }
        return app.head.accept(this).accept(InlineAppVisitor(args, table, block))
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
        val instance = qualifiedNode.instance.accept(this)

        if(instance !is HasMembers) {
            throw SemanticAnalysisException("node does not allow members", qualifiedNode)
        }

        return instance.getMember(qualifiedNode.qualifier)
    }

    override fun visit(varDecl: VarDecl): Node {
        val initialization = varDecl.init?.accept(this)

        // TODO: Put into parser: let{ typeName ->
        //                BaseTypes.values()
        //                    .firstOrNull { it.name == typeName }
        //                    ?: throw SemanticAnalysisException("unknown type $typeName", varDecl) }

        val type = varDecl.varType
            ?:initialization?.type
            ?:throw SemanticAnalysisException("missing type", varDecl)

        val newVarName = varNameGenerator.next()
        val newVarNode = IdNode(varDecl.trace, newVarName)

        if(initialization != null) {
            // first initialization because the variable is only used
            // after evaluating it.
            val assignment = App(varDecl.trace, Assign,
                listOf(newVarNode, type.convert(initialization)))

            block.add(assignment)
        }

        // now the declaration
        val newVarDecl = VarDecl(varDecl.trace, newVarName, type, null)
        block.add(newVarDecl)

        table.set(varDecl.name, newVarNode)

        return Nop(varDecl.trace)

        // returns a new varDecl: var $0: Type.
        // Init will be an assignment before, to allow reuse of unused value
        // no need to reserve the space before the actual assignment.

        // Example for unit test:

        // var a = { var b = 1; b }
        // this becomes
        // $0 = 1; var $0: Int; $1 = $0; var $1: Int;
        // when assigning memory, it is assigned backwards.
        // maintain a sorted set 'activeVars' by offset position. Remove var that is not used anymore. Always use max value for efficiency.
        // defragmentation :D

        // algorithm:
        // map<string, integer> varOffsets // keep all here.
        // map<integer, string> activeVars

        // run backwards
        // Every node receives an offset for its value. If it is a bool or a unit, well, don't care because size is 0.
    }

    override fun visit(idNode: IdNode): Node {
        return table[idNode.id] ?: throw SemanticAnalysisException("undefined", idNode)
    }

    override fun visit(block: Block): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(forStmt: For): Node {
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

    override fun visit(realNode: RealNode): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(stringNode: StringNode): Node {
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

    override fun visit(classEnv: ClassEnv): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funEnv: FunEnv): Node {
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