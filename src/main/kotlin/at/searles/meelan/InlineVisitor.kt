package at.searles.meelan

import at.searles.meelan.ops.Assign


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


class InlineVisitor(val parentTable: SymbolTable, val varNameGenerator: Iterator<String>): Visitor<Node> {
    val block = ArrayList<Node>()
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

		if(initialization != null) {
			val assignment = App(trace, Assign,
				listOf(newVarNode, type.convert(initialization)))

			addStmt(assignment)
		}

		val newVarDecl = VarDecl(trace, newVarName, type, null)
		addStmt(newVarDecl)

		setInTable(it.second.trace, it.first.id, newVarNode)
	}


    override fun visit(funDecl: FunDecl): Node {
        setInTable(funDecl.trace, funDecl.name, FunEnv(funDecl, table))
        return Nop(funDecl.trace)
    }

    override fun visit(classDecl: ClassDecl): Node {
        setInTable(classDecl.trace, classDecl.name, ClassEnv(classDecl, table))
        return Nop(classDecl.trace)
    }

    override fun visit(app: App): Node {
        val args = 
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

		initializeVar(varDecl.trace, VarParameter(varDecl.trace, varDecl.id, type), initialization)
		
        return Nop(varDecl.trace)
    }

    override fun visit(idNode: IdNode): Node {
        return table[idNode.id] ?: throw SemanticAnalysisException("undefined", idNode)
    }

    override fun visit(block: Block): Node {
        val innerVisitor = InlineVisitor(table, varNameGenerator)
		
		block.stmts.forEach ({
			innerVisitor.addStmt(it.accept(innerVisitor))}
		)
		
		val stmts = innerVisitor.block
		
		return Block(stmts).apply {
			type = stmts.isEmpty ? Type.Unit : stmts.last.type
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
		val inlinedCondition = ifStmt.cond.accept(this)
		
		if(inlinedCondition.type != Type.Bool) {
			throw new SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}
		
		val inlinedThenBranch = ifElse.thenBranch.accept(this)
		val inlinedElseBranch = ifElse.thenBranch.accept(this)

		val type = commonTypeOf(inlinedThenBranch.type, inlinedElseBranch.type) ?:
			throw SemanticAnalysisException("incompatible types in if-else statement", ifElse.trace)
			
		return IfElse(ifElse.trace, 
					inlinedCondition,
					type.convert(inlinedThenBranch),
					type.convert(inlinedElseBranch)).apply {
				this.type = type
		}
    }

    override fun visit(ifStmt: If): Node {
		val inlinedCondition = ifStmt.cond.accept(this)
		
		if(inlinedCondition.type != Type.Bool) {
			throw new SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}
		
		val inlinedBody = ifStmt.body.accept(this)
		
		if(inlinedBody.type != Type.Unit) {
			throw new SemanticAnalysisException("no return type expected", inlinedBody.trace)
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
		val inlinedCondition = whileStmt.cond.accept(this)
		
		if(inlinedCondition.type != Type.Bool) {
			throw new SemanticAnalysisException("boolean expected", inlinedCondition.trace)
		}
		
		val inlinedBody = whileStmt.body.accept(this)
		
		if(inlinedBody.type != Type.Unit) {
			throw new SemanticAnalysisException("no return type expected", inlinedBody.trace)
		}
		
		if(inlinedCondition is BoolNode) {
			if(inlinedCondition.value) {
				throw SemanticAnalysisException("infinite loop", whileStmt.trace)
			}
			
			return Nop(whileStmt.trace)
		}
		
		return While(whileStmt.trace, inlinedCondition, inlinedBody)
    }

    override fun visit(nop: Nop): Node {
		throw IllegalArgumentException("unreachable")
    }

    override fun visit(vectorNode: VectorNode): Node {
		if(vector.isEmpty()) {
			throw SemanticAnalysisException("empty vector", vectorNode.trace)
		}
		
		val inlinedVector = vectorNode.items.map(it.accept(this))
		// ensure common type of all items
		val type = inlinedVector.items.fold(inlinedVector.items.first.type) { type, item -> 
			commonTypeOf(type, item.type) ?: throw SemanticAnalysisException("incompatible type with previous elements", item.trace)
		}
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
