package at.searles.fractlang.semanticanalysis

import at.searles.fractlang.*
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.And
import at.searles.fractlang.ops.Not
import at.searles.fractlang.ops.Or
import at.searles.fractlang.parsing.FractlangParser
import at.searles.parsing.ParserLookaheadException
import at.searles.parsing.ParserStream
import at.searles.parsing.Trace

class SemanticAnalysisVisitor(parentTable: SymbolTable, val varNameGenerator: Iterator<String>):
    Visitor<Node> {
	val block = ArrayList<Node>()
    val table = parentTable.fork()

	fun setInTable(trace: Trace, id: String, value: Node) {
        if(!table.set(id, value)) {
            throw SemanticAnalysisException(
				"already defined",
				trace
			)
        }
	}

	fun addStmt(stmt: Node) {
		if(stmt !is Nop) {
			block.add(stmt)
		}
	}
	
	/**
	 * @param trace The trace of the whole declaration
	 * @param initialization optional assigned value. Must be inlined.
	 * @param varNode The declaration part. Must contain the proper type.
	 */
	fun initializeVar(trace: Trace, varNode: VarParameter, initialization: Node?) {
		val type = varNode.varType
				?: initialization?.type
				?: throw SemanticAnalysisException(
					"Could not determine type",
					varNode.trace
				)

		val newVarName = varNameGenerator.next()
		val newIdNode = IdNode(varNode.trace, newVarName).apply {
			this.type = type
		}

		if(initialization != null) {
			val assignment = Assignment(trace, newIdNode, type.convert(initialization))
			addStmt(assignment)
		}

		val newVarDecl = VarDecl(trace, newVarName, type, null)
		addStmt(newVarDecl)

		setInTable(varNode.trace, varNode.name, newIdNode)
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
        return app.head.accept(this).accept(
			InlineAppVisitor(
				app.trace,
				app.args,
				this
			)
        )
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
        val instance = qualifiedNode.instance.accept(this)

        if(instance is ObjectNode) {
			// It is an object or a complex number
			return instance.getMember(qualifiedNode.trace, qualifiedNode.qualifier)
		}

		throw SemanticAnalysisException(
			"not an object",
			qualifiedNode.trace
		)
    }

	override fun visit(valDecl: ValDecl): Node {
		val assignment = valDecl.init.accept(this)
		table.set(valDecl.name, assignment)
		return Nop(valDecl.trace)
	}

	override fun visit(varDecl: VarDecl): Node {
        val initialization = varDecl.init?.accept(this)

        val type = varDecl.varType
            ?:initialization?.type
            ?:throw SemanticAnalysisException(
				"missing type",
				varDecl.trace
			)

		if(type.vmCodeSize() == 0) {
			throw SemanticAnalysisException(
				"cannot create variable of this type",
				varDecl.trace
			)
		}

		initializeVar(varDecl.trace,
			VarParameter(varDecl.trace, varDecl.name, type), initialization)
		
        return Nop(varDecl.trace)
    }

    override fun visit(idNode: IdNode): Node {
		val node = table[idNode.trace, idNode.id]
			?: throw SemanticAnalysisException("${idNode.id} is undefined", idNode.trace)

		// Injected value?
		if(node is ExternNode) {
			return node.accept(this)
		}

		return node
	}

    override fun visit(block: Block): Node {
        val innerVisitor = SemanticAnalysisVisitor(table, varNameGenerator)
		
		block.stmts.forEach {
			innerVisitor.addStmt(it.accept(innerVisitor))
		}

		val stmts = innerVisitor.block

		if(stmts.size == 1 && stmts.last().type != BaseTypes.Unit) {
			return stmts.last()
		}

		return Block(block.trace, stmts.filter { it !is Nop }).apply {
			type = if(stmts.isEmpty()) BaseTypes.Unit else stmts.last().type
		}
    }

    override fun visit(classEnv: ClassEnv): Node {
        return classEnv.accept(
			InlineAppVisitor(
				classEnv.trace,
				emptyList(),
				this
			)
		)
    }

    override fun visit(funEnv: FunEnv): Node {
        return funEnv.accept(
			InlineAppVisitor(
				funEnv.trace,
				emptyList(),
				this
			)
		)
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
			throw SemanticAnalysisException(
				"boolean expected",
				inlinedCondition.trace
			)
		}
		
		val inlinedThenBranch = ifElse.thenBranch.accept(this)
		val inlinedElseBranch = ifElse.elseBranch.accept(this)

		val type = inlinedThenBranch.type.commonType(inlinedElseBranch.type) ?:
			throw SemanticAnalysisException(
				"incompatible types in if-else statement",
				ifElse.trace
			)

		if(inlinedCondition is BoolNode) {
			return if(inlinedCondition.value) {
				inlinedThenBranch
			} else {
				inlinedElseBranch
			}
		}

		if(inlinedThenBranch is BoolNode) {
			/*
			 * if(a) true else b = a and b.
			 */

			return if(inlinedThenBranch.value) {
				And.createApp(ifElse.trace, listOf(inlinedCondition, inlinedElseBranch))
			} else {
				And.createApp(ifElse.trace, listOf(
					Not.createApp(ifElse.trace, listOf(inlinedCondition)),
					inlinedElseBranch)
				)
			}
		}

		if(inlinedElseBranch is BoolNode) {
			/*
			 * if(a) b else true  ==  not a or b
			 * if(a) b else false ==  a or b
			 */
			return if(inlinedElseBranch.value) {
				Or.createApp(ifElse.trace, listOf(
					Not.createApp(ifElse.trace, listOf(inlinedCondition)),
					inlinedThenBranch)
				)
			} else {
				Or.createApp(ifElse.trace, listOf(inlinedCondition, inlinedThenBranch))
			}
		}


		return IfElse(
			ifElse.trace,
			inlinedCondition,
			type.convert(inlinedThenBranch),
			type.convert(inlinedElseBranch)
		).apply {
				this.type = type
		}
    }

    override fun visit(ifStmt: If): Node {
		val inlinedCondition = ifStmt.condition.accept(this)
		
		if(inlinedCondition.type != BaseTypes.Bool) {
			throw SemanticAnalysisException(
				"boolean expected",
				inlinedCondition.trace
			)
		}
		
		val inlinedBody = ifStmt.thenBranch.accept(this)
		
		if(inlinedBody.type != BaseTypes.Unit) {
			throw SemanticAnalysisException(
				"no return type expected",
				inlinedBody.trace
			)
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
			throw SemanticAnalysisException(
				"boolean expected",
				inlinedCondition.trace
			)
		}

		val inlinedBody = whileStmt.body.accept(this)
		
		if(inlinedBody.type != BaseTypes.Unit) {
			throw SemanticAnalysisException(
				"no return type expected",
				inlinedBody.trace
			)
		}
		
		if(inlinedCondition is BoolNode) {
			if(inlinedCondition.value) {
				throw SemanticAnalysisException(
					"infinite loop",
					whileStmt.trace
				)
			}
			
			return Block(whileStmt.trace, listOf()).apply { type = BaseTypes.Unit }
		}
		
		return While(whileStmt.trace, inlinedCondition, inlinedBody)
    }

	override fun visit(nop: Nop): Node {
		return Block(nop.trace, listOf()).apply { type = BaseTypes.Unit }
	}

	override fun visit(objectNode: ObjectNode): Node {
		// TODO is it?
		throw IllegalArgumentException("unreachable")
	}

    override fun visit(vectorNode: VectorNode): Node {
		if(vectorNode.items.isEmpty()) {
			throw SemanticAnalysisException(
				"empty vector",
				vectorNode.trace
			)
		}
		
		val inlinedVectorItems = vectorNode.items.map { it.accept(this) }

		// ensure common type of all items
		inlinedVectorItems.fold(inlinedVectorItems.first().type) { type, item ->
			type.commonType(item.type)
				?: throw SemanticAnalysisException(
					"incompatible type with previous elements",
					item.trace
				)
		}

		return VectorNode(vectorNode.trace, inlinedVectorItems)
    }

    override fun visit(forStmt: For): Node {
		// syntax: for ( (var)? id (':' type)? in collection )
		// collection is either a vector or a 1..3 or a 1 until range.
		// TODO sometimes...
		throw SemanticAnalysisException(
			"not implemented",
			forStmt.trace
		)
    }

    override fun visit(varParameter: VarParameter): Node {
		error("not applicable")
    }

	override fun visit(assignment: Assignment): Node {
		val lhs = assignment.lhs.accept(this)

		if(lhs !is IdNode) {
			throw SemanticAnalysisException(
				"Cannot assign to this value",
				lhs.trace
			)
		}

		val rhs = assignment.rhs.accept(this)

		return Assignment(assignment.trace, lhs, rhs)
	}

	override fun visit(externDecl: ExternDecl): Node {
		table.declareExtern(externDecl.trace, externDecl.name, externDecl.description, externDecl.expr)
		return Nop(externDecl.trace)
	}

	override fun visit(externNode: ExternNode): Node {
		try {
			// FIXME is extern a bad word? inject a: "Some value" = 12 better?
			val stream = ParserStream.fromString(externNode.expr)
			val exprAst = FractlangParser.expr.parse(stream)
				?: throw SemanticAnalysisException("Could not parse extern value", externNode.trace)

			if(!FractlangParser.eof.recognize(stream)) {
				throw SemanticAnalysisException("Extern value not fully parsed", externNode.trace)
			}

			return exprAst.accept(SemanticAnalysisVisitor(AllowImplicitExternsFacade(externNode.id, this), varNameGenerator))
		} catch(e: ParserLookaheadException) {
			throw SemanticAnalysisException("Parser error in extern value: ${e.message}", externNode.trace)
		} catch (e: SemanticAnalysisException) {
			throw SemanticAnalysisException("Semantic error in extern value: ${e.message}", externNode.trace)
		}
	}
}
