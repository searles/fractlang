package at.searles.fractlang.vm

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.ops.*

object VmGenerator {

	private val header = """
#include "complex.rsh"

static float3 createResult(int layer, double2 value, double height) {
    float2 fValue = convert_float2(value);

    fValue = fValue - floor(fValue);

    return (float3) {
        (float) (fValue.x + layer),
        (float) fValue.y,
        (float) height
    };
}

int *code;
uint32_t codeSize;

static float3 valueAt(double2 pt) {
	uint32_t pc = 0;
	
	int data[256];
	
	float3 result;
	
	while(pc < codeSize) {
		switch(code[pc]) {
""".trimIndent()

	private val footer = """
		}
	}
	
	return result;
}
	""".trimIndent()

	fun generateVm(ops: List<VmBaseOp>): String {
		val sb = StringBuilder(header).append("\n")

		var offset = 0

		ops.forEach {
			sb.append(generateOp(it, offset))
			offset += it.countArgKinds
		}

		sb.append(footer)

		return sb.toString()
	}

	fun access(relativeOffset: Int, argKind: BaseOp.ArgKind): String {
		// special treatment for Intel CPUs.
		if(argKind.isConst && argKind.type == BaseTypes.Cplx) {
			val arg0 = access(relativeOffset, BaseOp.ArgKind(BaseTypes.Real, true))
			val arg1 = access(relativeOffset + 2, BaseOp.ArgKind(BaseTypes.Real, true))

			return "((double2) {$arg0, $arg1})"
		}

		val intAccess = if(argKind.isConst) "code[pc + $relativeOffset]" else "data[code[pc + $relativeOffset]]"
		
		return when(argKind.type) {
			BaseTypes.Int -> intAccess
			BaseTypes.Real -> "(*((double*) (&$intAccess)))"
			BaseTypes.Cplx -> "(*((double2*) (&$intAccess)))"
			else -> error("must be a base type")
		}
	}
	
	fun generateOp(op: VmBaseOp, offset: Int): String {
		val sb = StringBuilder().append("            // === $op ===\n")

		for(index in 0 until op.countArgKinds) {
			val config = op.getArgKindAt(index)
			
			var relativeOffset = 1 // 0 is the instruction code
			
			val args = config.map { arg ->
				access(relativeOffset, arg).also {
					relativeOffset += if(arg.isConst) arg.type.vmCodeSize() else 1
				}
			}

			sb.append("            // $op: ")

			sb.append(config.map{if(it.isConst) "${it.type}" else "*${it.type}"}).append("\n")

			sb.append("            case ${offset + index}: ")
			
			sb.append(
				when(op.signatures[0].returnType) {
					BaseTypes.Bool -> generateBoolCall(
						op,
						index,
						args,
						relativeOffset
					)
					BaseTypes.Unit -> generateUnitCall(
						op,
						index,
						args,
						relativeOffset
					)
					else -> generateExprCall(
						op,
						index,
						args,
						relativeOffset
					)
				}
			)
			
			sb.append("break; \n")
		}

		return sb.toString()
	}

	private fun generateExprCall(op: VmBaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val ret = access(
			relativeOffset,
			BaseOp.ArgKind(op.getSignatureAt(offset).returnType, false)
		)
	
		val call = when(op) {
			is Add -> "$ret = ${args[0]} + ${args[1]}; "
			is Sub -> "$ret = ${args[0]} - ${args[1]}; "
			is Mul -> generateMul(offset, args, ret)
			is Div -> generateDiv(offset, args, ret)
			is Mod -> "$ret = ${args[0]} % ${args[1]}; "
			is Neg -> "$ret = -${args[0]}; "
			is Reciprocal -> generateRecip(offset, args[0], ret)
			is Point -> "$ret = pt; "
			is RealPart -> "$ret = ${args[0]}.x; "
			is ImaginaryPart -> "$ret = ${args[0]}.y; "
			is Abs -> generateAbs(offset, args[0], ret)
			is Sqrt -> "$ret = sqrt(${args[0]}); "
			is Exp -> "$ret = exp(${args[0]}); "
			is Log -> "$ret = log(${args[0]}); "
			is Sin -> "$ret = sin(${args[0]}); "
			is Cos -> "$ret = cos(${args[0]}); "
			is Sinh -> "$ret = sinh(${args[0]}); "
			is Cosh -> "$ret = cosh(${args[0]}); "
			is Pow -> "$ret = pow(${args[0]}, ${args[1]}); "
			is Rad -> "$ret = rad(${args[0]}); "
			is Arc -> "$ret = arc(${args[0]}); "
			is Cons -> "$ret = (double2) {${args[0]}, ${args[1]}}; "
			is Rabs -> "$ret = rabs(${args[0]}); "
			is Iabs -> "$ret = iabs(${args[0]}); "
			is Conj -> "$ret = conj(${args[0]}); "
			is ToReal -> "$ret = (double) ${args[0]}; "
			else -> throw IllegalArgumentException("not implemented: $op")
		}
		
		return call + "pc += ${relativeOffset + 1}; "
	}

	private fun generateBoolCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		val trueLabel = "code[pc + $relativeOffset]"
		val falseLabel = "code[pc + ${relativeOffset + 1}]"

		return when(op) {
			is Equal -> "if(${args[0]} == ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			is Less -> "if(${args[0]} < ${args[1]}) pc = $trueLabel; else pc = $falseLabel;"
			is Next -> "if(++${args[1]} < ${args[0]}) pc = $trueLabel; else pc = $falseLabel;"
			else -> throw IllegalArgumentException("not implemented: $op")
		}
	}
	
	private fun generateUnitCall(op: BaseOp, offset: Int, args: List<String>, relativeOffset: Int): String {
		return when(op) {
			is Jump -> "pc = ${args[0]}; "
			is Assign -> "${args[0]} = ${args[1]}; pc += $relativeOffset; "
			is SetResult -> "result = createResult(${args[0]}, ${args[1]}, ${args[2]}); pc += $relativeOffset; "
			// TODO relative jump: return "pc = code[pc + relativeOffset + ${args[0]}]"
			else -> throw IllegalArgumentException("not implemented: $op")
		}
	}

	private fun generateMul(offset: Int, args: List<String>, ret: String): String {
		val signature = Mul.getSignatureAt(offset)

		return if(signature.returnType == BaseTypes.Cplx) {
			"$ret = cmul(${args[0]}, ${args[1]}); "
		} else {
			"$ret = ${args[0]} * ${args[1]}; "
		}
	}

	private fun generateDiv(offset: Int, args: List<String>, ret: String): String {
		val signature = Div.getSignatureAt(offset)

		return if(signature.returnType == BaseTypes.Cplx) {
			"$ret = cdiv(${args[0]}, ${args[1]}); "
		} else {
			"$ret = ${args[0]} / ${args[1]}; "
		}
	}

	private fun generateRecip(offset: Int, arg: String, ret: String): String {
		val signature = Reciprocal.getSignatureAt(offset)

		return if(signature.returnType == BaseTypes.Cplx) {
			"$ret = cdiv((double2) {1., 0.}, $arg); "
		} else {
			"$ret = 1.0 / $arg; "
		}
	}

	private fun generateAbs(offset: Int, arg: String, ret: String): String {
		val signature = Abs.getSignatureAt(offset)

		return if(signature.returnType == BaseTypes.Cplx) {
			"$ret = cabs($arg); "
		} else {
			"if($arg < 0) $ret = -$arg; else $ret = $arg; "
		}
	}
}
