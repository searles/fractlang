stmts.backwards.forEach { stmt ->
	if(stmt is App) {
		app.args.filterIsInstance<IdNode>().forEach { arg ->
			if(!offsets.containsKey(arg.id)) {
				// not with filter because arguments are possibly identical
				// thus causing side effects
				val lastEntry = actives.lastEntry() // on top of last item.
				val offset = lastEntry.key + lastEntry.value.type!!.byteSize
				offsets[arg.id] = offset
				actives[offset] = arg
			}
		}
	} else if(stmt is VarDecl) {
		// remove from actives.
		val varName = stmt.id
		var removedEntry = actives.remove(offsets[varName]!!)
		require(varName == removedEntry.id)
	}
}


fun countArgKinds(signatureIndex: Int): Int {
	var count = 1
	var hasLValue = false

	for(i = 0 until signature.args.size) {
		if(!isLValue(i)) {
			count *= 2
		} else {
			hasLValue = true
		}
	}
	
	return if(!hasLValue) count - 1 else count
}

fun getArgKind(signatureIndex: Int, var index: Int): Array<Boolean> {
	argIsConst = Array<Boolean>(signature.size)

	for(i = 0 until signature.args.size) {
		if(isLValue(i)) {
			argIsConst[i] = false
		} else {
			argIsConst[i] = (index % 2) == 1
			index /= 2
		}
	}

	return argIsVar
}

fun getArgKindIndex(signatureIndex: Int, val args: List<Node>): Int {
	index = 0

	for(i = signature.args.size - 1 .. 0) {
		if(!isLValue(i)) {
			index *= 2
			
			if(args[i] is ConstValue) {
				index += 1
			}
		} else {
			require(args[i] is IdNode)
		}
	}

	return index
}

generateAccessor(offset: Int, type: Type, isLValue: Boolean): String {
}

nextOffset(
