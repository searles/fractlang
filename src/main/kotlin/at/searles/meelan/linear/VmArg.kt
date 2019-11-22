package at.searles.meelan.linear

import at.searles.meelan.nodes.Node

interface VmArg {
	// {IntNode, RealNode, CplxNode, IdNode, Label}
	fun vmCodeSize(): Int
	fun addToVmCode(vmCode: VmCode)

	interface Num: VmArg {
		override fun vmCodeSize(): Int {
			return (this as Node).type.vmCodeSize()
		}
	}
}
