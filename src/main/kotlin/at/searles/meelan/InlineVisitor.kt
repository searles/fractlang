package at.searles.meelan

import at.searles.meelan.ops.Assign
import at.searles.parsing.Trace


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


class InlineVisitor(parentTable: SymbolTable, val varNameGenerator: Iterator<String>): Visitor<Node> {
	private val block = ArrayList<Node>()
    val table = parentTable.fork()

	fun setInTable(trace: Trace, id: String, value: Node) {
        if(!table.set(id, value)) {
            throw SemanticAnalysisException("already defined", trace)
        }
	}

	fun addStmt(stmt: Node) {
		block.add(stmt)
	}
	
	/**
	 * @param trace The trace of the whole declaration
	 * @param initialization optional assigned value. Must be inlined.
	 * @param varNode The declaration part. Must contain the proper type.
	 */
	fun initializeVar(trace: Trace, varNode: VarParameter, initialization: Node?) {
		val newVarName = varNameGenerator.next()
		val newVarNode = VarParameter(varNode.trace, newVarName, varNode.type)

		val type = varNode.varType
				?: initialization?.type
				?: throw SemanticAnalysisException("Could not determine type", varNode.trace)

		if(initialization != null) {
			val assignment = App(trace, Assign,
				listOf(newVarNode, type.convert(initialization)))

			addStmt(assignment)
		}

		val newVarDecl = VarDecl(trace, newVarName, type, null)
		addStmt(newVarDecl)

		setInTable(varNode.trace, varNode.name, newVarNode)
	}


    override fun visit(funDecl: FunDecl): Node {
        setInTable(funDecl.trace, funDecl.name, FunEnv(funDecl, table))
        return Nop(funDecl.trace)
    }

    override fun visit(classDecl: ClassDecl): Node {
        setInTable(classDecl.trace, classDecl.name, ClassEnv(classDecl, table))
        return Nop(classDecl.trace)
    }

	override fun visit(opNode: OpNode): Node {
		return opNode
	}

	override fun visit(app: App): Node {
        return app.head.accept(this).accept(InlineAppVisitor(app.trace, app.args, this))
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
        val instance = qualifiedNode.instance.accept(this)

        if(instance !is HasMembers) {
            throw SemanticAnalysisException("node does not allow members", qualifiedNode.trace)
        }

        return instance.getMember(qualifiedNode.qualifier)
    }

	override fun visit(valDecl: ValDecl): Node {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun visit(varDecl: VarDecl): Node {
        val initialization = varDecl.init?.accept(this)

        // TODO: Put into parser: let{ typeName ->
        //                BaseTypes.values()
        //                    .firstOrNull { it.name == typeName }
        //                    ?: throw SemanticAnalysisException("unknown type $typeName", varDecl) }

        val type = varDecl.varType
            ?:initialization?.type
            ?:throw SemanticAnalysisException("missing type", varDecl.trace)

		initializeVar(varDecl.trace, VarParameter(varDecl.trace, varDecl.name, type), initialization)
		
        return Nop(varDecl.trace)
    }

    override fun visit(idNode: IdNode): Node {
		// FIXME find others
        return table[idNode.id] ?: throw SemanticAnalysisException("undefined", idNode.trace)
    }

    override fun visit(block: Block): Node {
        val innerVisitor = InlineVisitor(table, varNameGenerator)
		
		block.stmts.forEach {
			innerVisitor.addStmt(it.accept(innerVisitor))
		}

		val stmts = innerVisitor.block
		
		return Block(block.trace, stmts).apply {
			type = if(stmts.isEmpty()) BaseTypes.Unit else stmts.last().type
		}
    }

    override fun visit(classEnv: ClassEnv): Node {
        return classEnv.accept(InlineAppVisitor(classEnv.trace, emptyList(), this))
    }

    override fun visit(funEnv: FunEnv): Node {
        return funEnv.accept(InlineAppVisitor(funEnv.trace, emptyList(), this))
    }

	override fun visit(intNode: IntNode): Node {
        return intNode
    }

    override fun visit(realNode: RealNode): Node {
        return realNode
    }

    override fun visit(cplxNode: CplxNode): Node {
        return cplxNode
    }

    override fun visit(boolNode: BoolNode): Node {
		return boolNode
    }

    override fun visit(stringNode: StringNode): Node {
		return stringNode
    }

    override fun visit(ifElse: IfElse): Node {
		val inlinedCondition = ifElse.condition.accept(this)
		
		if(inlinedCondition.type != BaseTypes.Bool) {
			throw SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}
		
		val inlinedThenBranch = ifElse.thenBranch.accept(this)
		val inlinedElseBranch = ifElse.thenBranch.accept(this)

		val type = inlinedThenBranch.type.commonType(inlinedElseBranch.type) ?:
			throw SemanticAnalysisException("incompatible types in if-else statement", ifElse.trace)
			
		return IfElse(ifElse.trace, 
					inlinedCondition,
					type.convert(inlinedThenBranch),
					type.convert(inlinedElseBranch)).apply {
				this.type = type
		}
    }

    override fun visit(ifStmt: If): Node {
		val inlinedCondition = ifStmt.condition.accept(this)
		
		if(inlinedCondition.type != BaseTypes.Bool) {
			throw SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}
		
		val inlinedBody = ifStmt.thenBranch.accept(this)
		
		if(inlinedBody.type != BaseTypes.Unit) {
			throw SemanticAnalysisException("no return type expected", inlinedBody.trace)
		}
		
		if(inlinedCondition is BoolNode) {
			if(!inlinedCondition.value) {
				return Nop(ifStmt.trace)
			}
			
			return inlinedBody
		}
		
		return If(ifStmt.trace, inlinedCondition, inlinedBody)
    }

    override fun visit(whileStmt: While): Node {
		val inlinedCondition = whileStmt.condition.accept(this)
		
		if(inlinedCondition.type != BaseTypes.Bool) {
			throw SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}

		val inlinedBody = whileStmt.body.accept(this)
		
		if(inlinedBody.type != BaseTypes.Unit) {
			throw SemanticAnalysisException("no return type expected", inlinedBody.trace)
		}
		
		if(inlinedCondition is BoolNode) {
			if(inlinedCondition.value) {
				throw SemanticAnalysisException("infinite loop", whileStmt.trace)
			}
			
			return Nop(whileStmt.trace)
		}
		
		return While(whileStmt.trace, inlinedCondition, inlinedBody)
    }

	override fun visit(objectNode: ObjectNode): Node {
		// TODO is it?
		throw IllegalArgumentException("unreachable")
	}

	override fun visit(nop: Nop): Node {
		throw IllegalArgumentException("unreachable")
    }

    override fun visit(vectorNode: VectorNode): Node {
		if(vectorNode.items.isEmpty()) {
			throw SemanticAnalysisException("empty vector", vectorNode.trace)
		}
		
		val inlinedVectorItems = vectorNode.items.map { it.accept(this) }

		// ensure common type of all items
		inlinedVectorItems.fold(inlinedVectorItems.first().type) { type, item ->
			type.commonType(item.type)
				?: throw SemanticAnalysisException("incompatible type with previous elements", item.trace)
		}

		return VectorNode(vectorNode.trace, inlinedVectorItems)
    }

    override fun visit(forStmt: For): Node {
		// syntax: for ( (var)? id (':' type)? in collection )
		// collection is either a vector or a 1..3 or a 1 until range.
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varParameter: VarParameter): Node {
		throw IllegalArgumentException("unreachable")
    }
}
