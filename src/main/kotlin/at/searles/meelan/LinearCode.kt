class LinearCode() {
	val code = ArrayList<CodeLine>()

	var offset: Int = 0
		private set()

	fun addLabel(label: Label) {
		label.offset = offset
		code.add(label)
	}

	fun addInstruction(instruction: VmInstruction) {
		code.add(instruction)
		offset += instruction.vmCodeSize()
	}
	
	fun alloc(alloc: Alloc) {
		code.add(alloc)
	}
}
