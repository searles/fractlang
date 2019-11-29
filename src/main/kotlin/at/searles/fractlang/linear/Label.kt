package at.searles.fractlang.linear

import at.searles.fractlang.vm.VmArg
import at.searles.fractlang.vm.VmCodeAssembler

class Label: VmArg, CodeLine {
	var offset: Int = -1

	override fun vmCodeSize(): Int = 1

	override fun addToVmCode(vmCodeAssembler: VmCodeAssembler) {
		require(offset != -1)
		vmCodeAssembler.add(offset)
	}

	override fun toString(): String {
		return "@$offset"
	}
}
