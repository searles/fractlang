package at.searles.meelan.linear

import at.searles.meelan.ops.BaseOp

class VmCode(val linearCode: LinearCode, val instructions: List<BaseOp>) {
	
	val instructionOffsets = createInstructionOffsets(instructions)
	val memoryAddress: Map<String, Int> = createVariableOffsets(codeLines)
	
	val vmCode: List<Int> = ArrayList(linearCode.offset)
	
	init {
		linearCode.code.filterIsInstance<VmInstruction>().forEach { it.addToVmCode(this) }
	}

	fun add(code: Int) {
		vmCode.add(code)
	}

	fun add(real: Double) {
		// TODO
		vmCode.add(code)
	}

	fun add(cplx: Cplx) {
		// TODO
		vmCode.add(code)
	}
	
	fun add(id: String) {
		vmCode.add(memoryAddress[id])
	}

	/**
	 * Adds fn call
	 */
	fun add(op: BaseOp, index: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	companion object {
		fun createInstructionOffsets(instructions: List<BaseOp>): Map<BaseOp, Int> {
			val retMap = LinkedHashMap<BaseOp, Int>()
			instructions.fold(0) { offset, op -> run { retMap.put(op, offset); offset += op.callKindsCount() } }
			return retMap
		}
		
		fun createVariableOffsets(codeLines: List<CodeLine>): Map<String, Int> {
			// TODO
		}
	}
}
