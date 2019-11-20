package at.searles.meelan.linear

interface VmArg {
	// {IntNode, RealNode, CplxNode, IdNode, at.searles.meelan.linear.Label}
	fun vmCodeSize(): Int
	fun addToVmCode(vmCode: VmCode)
}
