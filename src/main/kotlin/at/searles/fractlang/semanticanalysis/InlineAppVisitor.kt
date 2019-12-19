package at.searles.fractlang.semanticanalysis

import at.searles.fractlang.Visitor
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Mul
import at.searles.parsing.Trace

/**
 * args are not inlined
 */
class InlineAppVisitor(val trace: Trace, private val args: List<Node>, private val parentVisitor: SemanticAnalysisVisitor):
    Visitor<Node> {
    private val inlinedArgs: List<Node> by lazy {
		args.map { it.accept(parentVisitor) }
	}

    private fun defineArgs(parameters: List<Node>, innerVisitor: SemanticAnalysisVisitor) {
        parameters.zip(inlinedArgs).forEach {

            val parameter = it.first
            val argument = it.second

            when (parameter) {
                is IdNode -> innerVisitor.setInTable(it.second.trace, parameter.id, it.second)
                is VarParameter -> innerVisitor.initializeVar(argument.trace, parameter, argument)
                else -> throw IllegalArgumentException("parameters must be Id or Var")
            }
        }
    }
	
	private fun checkArity(trace: Trace, expected: Int) {
        if(expected != args.size) {
            throw SemanticAnalysisException(
                "bad number of arguments",
                trace
            )
        }
	}
	
    override fun visit(funEnv: FunEnv): Node {
        checkArity(trace, funEnv.decl.parameters.size)

        defineArgs(funEnv.decl.parameters, parentVisitor)

        val innerVisitor = SemanticAnalysisVisitor(
            funEnv.table,
            parentVisitor.varNameGenerator
        )

        return funEnv.decl.body.accept(innerVisitor)
    }

    override fun visit(classEnv: ClassEnv): Node {
        checkArity(trace, classEnv.decl.parameters.size)

        val innerVisitor = SemanticAnalysisVisitor(
            classEnv.table,
            parentVisitor.varNameGenerator
        )

        defineArgs(classEnv.decl.parameters, innerVisitor)

        (classEnv.decl.body as Block).stmts.forEach {
            innerVisitor.addStmt(it.accept(innerVisitor))
        }

        // FIXME Check. This was a quick fix.
        innerVisitor.block.forEach {
            parentVisitor.addStmt(it)
        }

        // parentVisitor.addStmt(Block(classEnv.trace, innerVisitor.block))

        return ObjectNode(trace, innerVisitor.table.top())
    }

    override fun visit(opNode: OpNode): Node {
        return opNode.op.apply(trace, inlinedArgs)
    }

    override fun visit(vectorNode: VectorNode): Node {
		// this could be something like [1,2,3][4]
		
		// TODO sometimes.
		throw SemanticAnalysisException(
            "lists are not yet supported",
            vectorNode.trace
        )
    }

	private fun createImplicit(head: Node): Node {
		// all implicits allow only one argument
		checkArity(trace, 1)
		
		if(inlinedArgs[0] is VectorNode) {
            // TODO sometimes.
            throw SemanticAnalysisException(
                "lists are not yet supported",
                trace
            )
		}
		
		return Mul.apply(trace, listOf(head, inlinedArgs[0]))
	}

    override fun visit(ifElse: IfElse): Node {
        return createImplicit(ifElse)
    }

    override fun visit(intNode: IntNode): Node {
		return createImplicit(intNode)
    }

    override fun visit(realNode: RealNode): Node {
		return createImplicit(realNode)
    }

    override fun visit(cplxNode: CplxNode): Node {
		return createImplicit(cplxNode)
    }

    override fun visit(idNode: IdNode): Node {
        return createImplicit(idNode)
    }

    override fun visit(app: App): Node {
		// for example (sin x) y?
        return createImplicit(app)
    }

    override fun visit(block: Block): Node {
        return createImplicit(block)
    }

    override fun visit(classDecl: ClassDecl): Node {
		throw IllegalArgumentException("class declaration should have been inlined")
    }

    override fun visit(funDecl: FunDecl): Node {
		throw IllegalArgumentException("fun declaration should have been inlined")
    }

    override fun visit(varDecl: VarDecl): Node {
		throw IllegalArgumentException("var declaration should have been inlined")
    }

    override fun visit(valDecl: ValDecl): Node {
        throw IllegalArgumentException("val declaration should have been inlined")
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
		throw IllegalArgumentException("qualified node should have been inlined")
    }

    override fun visit(varParameter: VarParameter): Node {
		throw IllegalArgumentException("unreachable")
    }

    override fun visit(objectNode: ObjectNode): Node {
        throw SemanticAnalysisException(
            "cannot use this as a function head",
            objectNode.trace
        )
    }

    override fun visit(nop: Nop): Node {
		// (var x = 1) y
        throw SemanticAnalysisException(
            "cannot use this as a function head",
            nop.trace
        )
    }

    override fun visit(forStmt: For): Node {
        throw SemanticAnalysisException(
            "cannot use for-statement as a function head",
            forStmt.trace
        )
    }

    override fun visit(whileStmt: While): Node {
        throw SemanticAnalysisException(
            "cannot use while-statement as a function head",
            whileStmt.trace
        )
    }

    override fun visit(ifStmt: If): Node {
        throw SemanticAnalysisException(
            "cannot use if-statement as a function head",
            ifStmt.trace
        )
    }

    override fun visit(stringNode: StringNode): Node {
		// any funny use for that?
        throw SemanticAnalysisException(
            "cannot use string as a function head",
            stringNode.trace
        )
    }
	
    override fun visit(boolNode: BoolNode): Node {
        throw SemanticAnalysisException(
            "cannot use boolean as a function head",
            boolNode.trace
        )
    }

    override fun visit(assignment: Assignment): Node {
        throw SemanticAnalysisException(
            "cannot use assignment as a function head",
            assignment.trace
        )
    }

    override fun visit(externDecl: ExternDecl): Node {
        throw SemanticAnalysisException(
            "cannot use declaration as a function head",
            externDecl.trace
        )
    }

    override fun visit(externNode: ExternNode): Node {
        error("externs should be inlined in SemanticAnalysisVisitor")
    }
}
