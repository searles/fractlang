import at.searles.meelan.nodes.App
import at.searles.meelan.nodes.IdNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.VarDecl
import java.util.*
import kotlin.collections.HashMap

fun assignmem(stmts: List<Node>) {
	val offsets = HashMap<String, Int>()
	val actives = TreeMap<Int, IdNode>()

	stmts.reversed().forEach { stmt ->
		if (stmt is App) {
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
		} else if (stmt is VarDecl) {
			// remove from actives.
			val varName = stmt.name
			val removedEntry = actives.remove(offsets[varName]!!)
			require(varName == removedEntry?.id)
		}
	}
}