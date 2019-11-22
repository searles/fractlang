package at.searles.meelan

import at.searles.meelan.ops.*

class VmGenerator {
	fun access(type: Type, relativeOffset: Int, asConst: Boolean): String {
		val intAccess = if(asConst) "code[pc + $relativeOffset]" else "data[code[pc + $relativeOffset]]"
		
		return when(type) {
			BaseTypes.Int -> intAccess
			BaseTypes.Real -> "*((double*) (&$intAccess))"
			BaseTypes.Cplx -> "*((double2*) (&$intAccess))"
			else -> error("must be a base type")
		}
	}
	
	fun generateOp(op: BaseOp, offset: Int) {
		val sb = StringBuilder()

		for(index in 0 until op.countKinds()) {
			val signature = op.getSignatureForIndex(index)
			val isConst = op.getIsConstArrayForIndex(index)
			
			var relativeOffset = 1 // 0 is the instruction code
			
			val args = signature.argTypes.zip(isConst).map { arg ->
				access(arg.first, relativeOffset, arg.second).also {
					relativeOffset += if(arg.second) arg.first.vmCodeSize() else 1
				}
			}

			sb.append("    case ${offset + index}: ")
			
			sb.append(
				when(signature.returnType) {
					BaseTypes.Bool -> generateBoolCall(op, signature, args, relativeOffset)
					BaseTypes.Unit -> generateUnitCall(op, signature, args, relativeOffset)
					else -> generateExprCall(op, signature, args, relativeOffset)
				}
			)
			
			sb.append("break; \n")
		}
	}

	private fun generateExprCall(op: BaseOp, signature: Signature, args: List<String>, relativeOffset: Int): String {
		val ret = access(signature.returnType, relativeOffset, false)
	
		val call = when(op) {
			is Add -> "$ret = ${args[0]} + ${args[1]}};"
			is Sub -> "$ret = ${args[0]} - ${args[1]}};"
			else -> throw IllegalArgumentException()
		}
		
		return call + "pc += ${relativeOffset + 1}";
	}

	private fun generateBoolCall(op: BaseOp, signature: Signature, args: List<String>, relativeOffset: Int): String {
		val trueLabel = "code[pc + $relativeOffset]"
		val falseLabel = "code[pc + $relativeOffset + 1]"

		return when(op) {
			is Less -> "if(${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			// TODO is Next -> "if(++${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			else -> throw IllegalArgumentException()
		}
	}
	
	private fun generateUnitCall(op: BaseOp, signature: Signature, args: List<String>, relativeOffset: Int): String {
		return when(op) {
			is Jump -> "pc = ${args[0]};"
			// TODO relative jump: return "pc = code[pc + relativeOffset + ${args[0]}]"
			else -> throw IllegalArgumentException()
		}
	}
}
