package at.searles.fractlang

import at.searles.fractlang.nodes.ExternNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.Nop
import at.searles.fractlang.nodes.OpNode
import at.searles.fractlang.ops.Op
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val instructions: Map<String, Op>, private val definedExternValues: Map<String, String>): SymbolTable {

    private val externValueMap = HashMap<String, ExternNode>()

    val externValues: Map<String, String>
        get() = externValueMap.mapValues { it.value.expr }

    val declaredItems: List<Pair<String, List<Node>>>
        get() = internalDeclaredItems

    private val internalDeclaredItems = ArrayList<Pair<String, List<Node>>>()

    override fun get(trace: Trace, id: String): Node? {
        if(externValueMap.containsKey(id)) {
            return externValueMap[id]
        }

        if(instructions.containsKey(id)) {
            return instructions.getValue(id).toNode(trace)
        }

        if(id.startsWith(innerDeclarationMarker)) {
            return OpNode(trace, DeclareOp(id.substring(innerDeclarationMarker.length)))
        }

        return null
    }

    override fun declareExtern(trace: Trace, name: String, description: String, expr: String) {
        if(externValueMap.containsKey(name)) {
            throw SemanticAnalysisException("extern $name already defined", trace)
        }

        val isDefault = !definedExternValues.containsKey(name)

        val node = ExternNode(trace, name, description, isDefault,
            if(isDefault) expr else definedExternValues.getValue(name))

        externValueMap[name] = node
    }

    private inner class DeclareOp(val id: String): Op {
        override fun apply(trace: Trace, args: List<Node>): Node {
            internalDeclaredItems.add(Pair(id, args))
            return Nop(trace)
        }
    }

    companion object {
        const val innerDeclarationMarker = "__"
    }
}