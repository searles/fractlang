class VmCode(val linearCode: LinearCode, instructions: List<BaseOp>) {
	
	val instructionOffsets = createInstructionOffsets(instructions)
	val memoryAddress: Map<String, Int> = createVariableOffsets(codeLines)	
	
	val vmCode: List<Int> = ArrayList<Int>(linearCode.offset)
	
	init {
		codeLines.filterInstance<VmInstruction>.forEach { it.addToVmCode(this) } 
	}

	fun add(code: Int) {
		vmCode.add(code)
	}

	fun add(real: Real) {
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
