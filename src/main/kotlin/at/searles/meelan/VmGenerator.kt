package at.searles.meelan

import at.searles.meelan.ops.*

class VmGenerator {
	fun access(relativeOffset: Int, config: BaseOp.ParameterConfig): String {
		val intAccess = if(config.isConst) "code[pc + $relativeOffset]" else "data[code[pc + $relativeOffset]]"
		
		return when(config.type) {
			BaseTypes.Int -> intAccess
			BaseTypes.Real -> "*((double*) (&$intAccess))"
			BaseTypes.Cplx -> "*((double2*) (&$intAccess))"
			else -> error("must be a base type")
		}
	}
	
	fun generateOp(op: BaseOp, offset: Int) {
		val sb = StringBuilder()

		for(index in 0 until op.countParameterConfigurations()) {
			val config = op.getParameterConfiguration(index)
			
			var relativeOffset = 1 // 0 is the instruction code
			
			val args = config.map { arg ->
				access(relativeOffset, arg).also {
					relativeOffset += if(arg.isConst) arg.type.vmCodeSize() else 1
				}
			}

			sb.append("    case ${offset + index}: ")
			
			sb.append(
				when(op.signatures[0].returnType) {
					BaseTypes.Bool -> generateBoolCall(op, offset, args, relativeOffset)
					BaseTypes.Unit -> generateUnitCall(op, offset, args, relativeOffset)
					else -> generateExprCall(op, offset, args, relativeOffset)
				}
			)
			
			sb.append("break; \n")
		}
	}

	private fun generateExprCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val ret = access(relativeOffset, BaseOp.ParameterConfig(op.getSignatureForIndex(offset).returnType, false))
	
		val call = when(op) {
			is Add -> "$ret = ${args[0]} + ${args[1]}};"
			is Sub -> "$ret = ${args[0]} - ${args[1]}};"
			else -> throw IllegalArgumentException()
		}
		
		return call + "pc += ${relativeOffset + 1}"
	}

	private fun generateBoolCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val trueLabel = "code[pc + $relativeOffset]"
		val falseLabel = "code[pc + $relativeOffset + 1]"

		return when(op) {
			is Less -> "if(${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			// TODO is Next -> "if(++${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			else -> throw IllegalArgumentException()
		}
	}
	
	private fun generateUnitCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		return when(op) {
			is Jump -> "pc = ${args[0]};"
			// TODO relative jump: return "pc = code[pc + relativeOffset + ${args[0]}]"
			else -> throw IllegalArgumentException()
		}
	}
}
