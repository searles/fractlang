class Label: VmArg, CodeLine {
	var offset: Int = -1

	fun vmCodeSize(): Int = 1

	fun addToVmCode(vmCode: VmCode) {
		require(offset != -1)
		vmCode.add(offset)
	}
}
