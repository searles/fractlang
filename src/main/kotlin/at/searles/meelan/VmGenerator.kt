package at.searles.meelan

import at.searles.meelan.ops.*

object VmGenerator {

	fun generateVm(ops: List<BaseOp>): String {
		val sb = StringBuilder()

		var offset = 0

		ops.forEach {
			sb.append(generateOp(it, offset))
			offset += it.countParameterConfigurations()
		}

		return sb.toString()
	}

	fun access(relativeOffset: Int, config: BaseOp.ParameterConfig): String {
		val intAccess = if(config.isConst) "code[pc + $relativeOffset]" else "data[code[pc + $relativeOffset]]"
		
		return when(config.type) {
			BaseTypes.Int -> intAccess
			BaseTypes.Real -> "*((double*) (&$intAccess))"
			BaseTypes.Cplx -> "*((double2*) (&$intAccess))"
			else -> error("must be a base type")
		}
	}
	
	fun generateOp(op: BaseOp, offset: Int): String {
		val sb = StringBuilder().append("    // $op at $offset\n")

		for(index in 0 until op.countParameterConfigurations()) {
			val config = op.getParameterConfiguration(index)
			
			var relativeOffset = 1 // 0 is the instruction code
			
			val args = config.map { arg ->
				access(relativeOffset, arg).also {
					relativeOffset += if(arg.isConst) arg.type.vmCodeSize() else 1
				}
			}

			sb.append("    // $op[$index]\n")
			sb.append("    case ${offset + index}: ")
			
			sb.append(
				when(op.signatures[0].returnType) {
					BaseTypes.Bool -> generateBoolCall(op, index, args, relativeOffset)
					BaseTypes.Unit -> generateUnitCall(op, index, args, relativeOffset)
					else -> generateExprCall(op, index, args, relativeOffset)
				}
			)
			
			sb.append("break; \n")
		}

		return sb.toString()
	}

	private fun generateExprCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val ret = access(relativeOffset, BaseOp.ParameterConfig(op.getSignatureForIndex(offset).returnType, false))
	
		val call = when(op) {
			is Add -> "$ret = ${args[0]} + ${args[1]}};"
			is Sub -> "$ret = ${args[0]} - ${args[1]}};"
			is Mul -> "$ret = ${args[0]} * ${args[1]}};"
			is Div -> "$ret = ${args[0]} / ${args[1]}};"
			is Mod -> "$ret = ${args[0]} % ${args[1]}};"
			is Neg -> "$ret = -${args[0]};"
			else -> throw IllegalArgumentException("not implemented: $op")
		}
		
		return call + "pc += ${relativeOffset + 1}; "
	}

	private fun generateBoolCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val trueLabel = "code[pc + $relativeOffset]"
		val falseLabel = "code[pc + $relativeOffset + 1]"

		return when(op) {
			is Equal -> "if(${args[0]} == ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			is Less -> "if(${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			// TODO is Next -> "if(++${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			else -> throw IllegalArgumentException("not implemented: $op")
		}
	}
	
	private fun generateUnitCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		return when(op) {
			is Jump -> "pc = ${args[0]};"
			is Assign -> "${args[0]} = ${args[1]}; pc += $relativeOffset; "
			// TODO relative jump: return "pc = code[pc + relativeOffset + ${args[0]}]"
			else -> throw IllegalArgumentException("not implemented: $op")
		}
	}
}
