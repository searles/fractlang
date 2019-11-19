class VmInstruction(op: BaseOp, index: Int, args: List<Arg>): CodeLine {
	fun vmCodeSize(): Int {
		return 1 + args.map { it.vmCodeSize() }.sum
	}
	
	fun addToVmCode(vmCode: VmCode): Unit {
		vmCode.add(op, index)
		args.forEach { it.addToVmCode(vmCode) }
	}
}
