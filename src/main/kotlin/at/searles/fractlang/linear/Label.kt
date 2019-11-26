package at.searles.fractlang.linear

import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmCode

class Label: VmArg, CodeLine {
	var offset: Int = -1

	override fun vmCodeSize(): Int = 1

	override fun addToVmCode(vmCode: VmCode) {
		require(offset != -1)
		vmCode.add(offset)
	}

	override fun toString(): String {
		return "@$offset"
	}
}
