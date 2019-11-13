package at.searles.meelan

import at.searles.meelan.ops.Assign
import at.searles.parsing.Trace

/**
 * args are not inlined
 */
class InlineAppVisitor(val trace: Trace, val args: List<Node>, val parentVisitor: InlineVisitor): Visitor<Node> {

	private val inlinedArgs: ListNode by lazy {
		args.map { it.accept(parentVisitor) }
	}

    private fun defineArgs(parameters: List<Node>, innerVisitor: InlineVisitor) {
        parameters.zip(inlinedArgs).forEach {
            if(it.first is IdNode) {
				innerVisitor.setInTable(it.second.trace, it.first.id, it.second)
			} else if(it.first is VarParameter) {
				val varNode = if(it.first.varType == null) {
					VarParameter(it.first.trace, it.first.id, it.second.type)
				} else {
					it.first
				}
				
				innerVisitor.initializeVar(it.second.trace, varNode, it.second)
            } else {
                throw IllegalArgumentException("parameters must be Id or Var!")
            }
        }
    }
	
	private fun checkArity(trace: Trace, expected: Int) {
        if(expected != args.size) {
            throw SemanticAnalysisException("bad number of arguments", trace)
        }
	}
	
    override fun visit(funEnv: FunEnv): Node {
        checkArity(funEnv.decl.parameters.size)

        val innerVisitor = InlineVisitor(funEnv.table, parentVisitor.varNameGenerator)

        defineArgs(funEnv.decl.parameters, innerVisitor)

        val returnValue = funEnv.decl.accept(innerTable, innerBlock))

        block.add(innerBlock)

        return returnValue
    }

    override fun visit(classEnv: ClassEnv): Node {
        checkArity(classEnv.decl.parameters.size)

        val innerVisitor = InlineVisitor(classEnv.table, parentVisitor.varNameGenerator)

        defineArgs(classEnv.decl.parameters, innerVisitor)

        val objectBlock = classDecl.block.accept(innerVisitor)

        parentVisitor.addStmt(objectBlock)

        return ObjectNode(innerVisitor.table.top())
    }

    override fun visit(op: OpNode): Node {
        return op.apply(trace, inlinedArgs)
    }

    override fun visit(vectorNode: VectorNode): Node {
		// this could be something like [1,2,3][4]
		
		// TODO sometimes.
		throw SemanticAnalysisException("lists are not yet supported")
    }

	private fun createImplicit(head: Node): Node {
		// all implicits allow only one argument
		checkArity(1)
		
		if(inlinedArgs.get(0) is VectorNode) {
			// FIXME
		}
		
		return Mul.apply(trace, listOf(factor, inlinedArgs.get(0)))
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

    override fun visit(idNode: IdNode): Node {
		throw IllegalArgumentException("id should have been inlined")
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
		throw IllegalArgumentException("qualified node should have been inlined")
    }

    override fun visit(varParameter: VarParameter): Node {
		throw IllegalArgumentException("unreachable")
    }

    override fun visit(nop: Nop): Node {
		// (var x = 1) y
        throw SemanticAnalysisException("cannot use this as a function head", nop.trace)
    }

    override fun visit(forStmt: For): Node {
        throw SemanticAnalysisException("cannot use for-statement as a function head", nop.trace)
    }

    override fun visit(whileStmt: While): Node {
        throw SemanticAnalysisException("cannot use while-statement as a function head", nop.trace)
    }

    override fun visit(ifStmt: If): Node {
        throw SemanticAnalysisException("cannot use if-statement as a function head", nop.trace)
    }

    override fun visit(stringNode: StringNode): Node {
		// any funny use for that?
        throw SemanticAnalysisException("cannot use string as a function head", nop.trace)
    }
	
    override fun visit(boolNode: BoolNode): Node {
        throw SemanticAnalysisException("cannot use boolean as a function head", nop.trace)
    }
}
