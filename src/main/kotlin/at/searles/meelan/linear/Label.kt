package at.searles.meelan.linear

class Label: VmArg, CodeLine {
	var offset: Int = -1

	override fun vmCodeSize(): Int = 1

	override fun addToVmCode(vmCode: VmCode) {
		require(offset != -1)
		vmCode.add(offset)
	}
}
