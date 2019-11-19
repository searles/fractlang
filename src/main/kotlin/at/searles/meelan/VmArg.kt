interface VmArg {
	// {IntNode, RealNode, CplxNode, IdNode, Label}
	fun vmCodeSize(): Int
	fun addToVmCode(vmCode: VmCode)
}
