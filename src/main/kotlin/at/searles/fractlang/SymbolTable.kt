package at.searles.fractlang

import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

interface SymbolTable {
    operator fun get(trace: Trace, id: String): Node?

    fun fork(): Mutable {
        return Mutable(this)
    }

    fun addExternValue(trace: Trace, name: String, description: String, expr: String)

    class Mutable(private val parent: SymbolTable): SymbolTable {
        private val map: MutableMap<String, Node> = HashMap()

        override fun get(trace: Trace, id: String): Node? {
            return map[id] ?: parent[trace, id]
        }

        override fun addExternValue(trace: Trace, name: String, description: String, expr: String) {
            parent.addExternValue(trace, name, description, expr)
        }

        fun set(id: String, value: Node): Boolean {
            if(map.containsKey(id)) {
                return false
            }

            map[id] = value

            return true
        }

        fun top(): Map<String, Node> {
            return map
        }
    }
}
