package at.searles.fractlang.vm

import at.searles.fractlang.linear.CodeLine
import at.searles.fractlang.ops.BaseOp

class VmInstruction(val op: BaseOp, val index: Int, val args: List<VmArg>): CodeLine {

	init {
		require(0 <= index && index < op.countArgKinds()) { "failed for $op" }
	}

	fun vmCodeSize(): Int {
		return args.fold(1) { sum, arg -> sum + arg.vmCodeSize() }
	}
	
	fun addToVmCode(vmCodeAssembler: VmCodeAssembler) {
		vmCodeAssembler.add(op, index)
		args.forEach { it.addToVmCode(vmCodeAssembler) }
	}

	override fun toString(): String {
		return "$op[$index] $args"
	}
}