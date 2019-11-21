package at.searles.meelan.linear

import at.searles.commons.math.Cplx
import at.searles.meelan.nodes.IdNode
import at.searles.meelan.ops.BaseOp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import java.lang.Integer



class VmCode(val linearCode: LinearCode, val instructions: List<BaseOp>) {
	
	val instructionOffsets = createInstructionOffsets(instructions)
	val memoryAddress: Map<String, Int> = createVariableOffsets(linearCode)
	
	val vmCode = ArrayList<Int>(linearCode.offset)
	
	init {
		linearCode.code.filterIsInstance<VmInstruction>().forEach { it.addToVmCode(this) }
	}

	fun add(code: Int) {
		vmCode.add(code)
	}

	fun add(real: Double) {
		val l = java.lang.Double.doubleToRawLongBits(real)
		// FIXME beware of big endian systems [are there any?]
		vmCode.add((l and 0x0ffffffffL).toInt())
		vmCode.add((l shr 32).toInt())
	}

	fun add(cplx: Cplx) {
		add(cplx.re())
		add(cplx.im())
	}
	
	fun add(id: String) {
		add(memoryAddress[id] ?: error(""))
	}

	/**
	 * Adds fn call
	 */
	fun add(op: BaseOp, index: Int) {
		// TODO
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	companion object {
		fun createVariableOffsets(linearCode: LinearCode): Map<String, Int> {
			val offsets = HashMap<String, Int>()
			val actives = TreeMap<Int, IdNode>()

			linearCode.code.reversed().forEach { stmt ->
				if (stmt is VmInstruction) {
					stmt.args.filterIsInstance<IdNode>().forEach { arg ->
						if (!offsets.containsKey(arg.id)) {
							// not with filter because arguments are possibly identical
							// thus causing side effects
							val lastEntry = actives.lastEntry() // on top of last item.
							val offset = lastEntry.key + lastEntry.value.type.byteSize()
							offsets[arg.id] = offset
							actives[offset] = arg
						}
					}
				} else if (stmt is Alloc) {
					// remove from actives.
					val varName = stmt.id
					val removedEntry = actives.remove(offsets[varName]!!)
					require(varName == removedEntry?.id)
				}
			}

			return offsets
		}


		fun createInstructionOffsets(instructions: List<BaseOp>): Map<BaseOp, Int> {
			val retMap = LinkedHashMap<BaseOp, Int>()

			instructions.fold(0) { offset, op ->
				run {
					retMap[op] = offset
					offset + op.countKinds()
				}
			}

			return retMap
		}
	}
}
