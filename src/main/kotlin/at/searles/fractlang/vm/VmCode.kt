package at.searles.fractlang.vm

import at.searles.commons.math.Cplx
import at.searles.fractlang.linear.Alloc
import at.searles.fractlang.linear.LinearCode
import at.searles.fractlang.nodes.IdNode
import at.searles.fractlang.ops.BaseOp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class VmCode(private val linearCode: LinearCode, instructions: List<BaseOp>) {

	// FIXME Currently there are sometimes instructions like $0 = $0.
	// FIXME Eg in var a = 1; var c = a;

	private val instructionOffsets = createInstructionOffsets(instructions)
	private val memoryOffsets = HashMap<String, Int>()
	
	val vmCode = ArrayList<Int>(linearCode.offset)
	
	init {
		initializeMemoryOffsets()
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
		add(memoryOffsets[id] ?: error("not in memory: $id"))
	}

	private fun addToMemoryOffsets(id: String, actives: TreeMap<Int, IdNode>): Int {
		val lastEntry = actives.lastEntry() // on top of last item.

		val offset = actives.run {
			if(isEmpty()) 0 else lastEntry.key + lastEntry.value.type.vmCodeSize()
		}

		memoryOffsets[id] = offset
		return offset
	}

	/**
	 * How this algorithm works:
	 * Traverse backwards. If a new variable is found, set it to access the current stack top.
	 * If on the way back you encounter an allocation of this variable, remove it.
	 */
	private fun initializeMemoryOffsets() {
		val actives = TreeMap<Int, IdNode>()

		linearCode.code.reversed().forEach { stmt ->
			if (stmt is VmInstruction) {
				stmt.args.filterIsInstance<IdNode>().forEach { arg ->
					if (!memoryOffsets.containsKey(arg.id)) {
						val offset = addToMemoryOffsets(arg.id, actives)
						actives[offset] = arg
					}
				}
			} else if (stmt is Alloc) {
				// remove from actives.
				if (!memoryOffsets.containsKey(stmt.id)) {
					addToMemoryOffsets(stmt.id, actives)
				}
				val varName = stmt.id
				val removedEntry = actives.remove(memoryOffsets[varName]!!)
				require(removedEntry == null || varName == removedEntry.id)
			}
		}
	}


	/**
	 * Adds fn call
	 */
	fun add(op: BaseOp, index: Int) {
		val opIndex = instructionOffsets[op] ?: error("missing ${op}")
		add(opIndex + index)
	}

	companion object {
		fun createInstructionOffsets(instructions: List<BaseOp>): Map<BaseOp, Int> {
			val retMap = LinkedHashMap<BaseOp, Int>()

			instructions.fold(0) { offset, op ->
				run {
					retMap[op] = offset
					offset + op.countArgKinds()
				}
			}

			return retMap
		}
	}
}
