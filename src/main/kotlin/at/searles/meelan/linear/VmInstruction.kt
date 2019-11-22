package at.searles.meelan.linear

import at.searles.meelan.ops.BaseOp

class VmInstruction(val op: BaseOp, val index: Int, val args: List<VmArg>): CodeLine {
	fun vmCodeSize(): Int {
		return args.fold(1) { sum, arg -> sum + arg.vmCodeSize() }
	}
	
	fun addToVmCode(vmCode: VmCode): Unit {
		vmCode.add(op, index)
		args.forEach { it.addToVmCode(vmCode) }
	}

	override fun toString(): String {
		return "$op[$index] $args"
	}
}
