package at.searles.meelan

class VmGenerator {
	fun access(type: BaseType, relativeOffset: Int, asConst: Boolean): String {
		val intAccess = if(asConst) "code[pc + $relativeOffset]" else "data[code[pc + $relativeOffset]]"
		
		when(type) {
			is BaseType.Int -> intAccess"
			is BaseType.Real -> "*((double*) (&$intAccess))"
			is BaseType.Cplx -> "*((double2*) (&$intAccess))"
			else -> throw IllegalArgumentException()
		}
	}
	
	fun generateOp(op: BaseOp, offset: Int) {
		for(index in 0 until op.kindCount()) {
			val signature = op.signatureForIndex(index)
			val isConst = op.isConstForIndex(index)
			
			var relativeOffset = 1 // 0 is the instruction code
			
			val args = signature.args.zip(isConst).map(arg ->
				access(arg.first, relativeOffset, arg.second).also {
					relativeOffset += if(arg.second) arg.first.vmCodeSize() else 1
				}
			})
			
			sb.append("    case ${offset + index}: ")
			
			sb.append(
				when(signature.returnType) {
					BaseType.Bool -> generateBoolCall(this, signature, args, relativeOffset)
					BaseType.Unit -> generateUnitCall(this, signature, args, relativeOffset)
					else -> generateExprCall(this, signature, args, relativeOffset)					
				}
			)
			
			sb.append("break; \n")
		}
	}

	fun generateExprCall(op: BaseOp, signature: Signature, args: List<String>, ret: String, relativeOffset: Int): String {
		val ret = access(signature.returnType, relativeOffset, false)
	
		val call = when(op) {
			is Add -> "$ret = ${args[0]} + ${args[1]}};"
			is Sub -> "$ret = ${args[0]} - ${args[1]}};"
			else -> throw IllegalArgumentException()
		}
		
		return call + "pc += ${relativeOffset + 1}";
	}

	fun generateBoolCall(op: BaseOp, signature: Signature, args: List<String>, relativeOffset: Int): String {
		val trueLabel = "code[pc + $relativeOffset]"
		val falseLabel = "code[pc + $relativeOffset + 1]"
		
		val call = when(op) {
			is Less -> "if(${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			is Next -> "if(++${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			else -> throw IllegalArgumentException()
		}

		return call
	}
	
	override fun generateUnitCall(op: Jump, args: List<String>, relativeOffset: Int): String {
		val call = when(op) {
			is Jump -> "pc = $arg[0];"
			// TODO relative jump: return "pc = code[pc + relativeOffset + ${args[0]}]"
			else -> throw IllegalArgumentException()
		}

		return call
	}
}
