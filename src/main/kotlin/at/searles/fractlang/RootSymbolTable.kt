package at.searles.fractlang

import at.searles.fractlang.nodes.ExternNode
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.OpNode
import at.searles.fractlang.ops.Op
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val instructions: Map<String, Op>, private val definedExternValues: Map<String, String>): SymbolTable {

    private val externValueMap = HashMap<String, ExternNode>()

    val externValues: Map<String, String>
        get() = externValueMap.mapValues { it.value.expr }

    override fun get(trace: Trace, id: String): Node? {
        if(externValueMap.containsKey(id)) {
            return externValueMap[id]
        }

        if(instructions.containsKey(id)) {
            return OpNode(trace, instructions.getValue(id))
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
}