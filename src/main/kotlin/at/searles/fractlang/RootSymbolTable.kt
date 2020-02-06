package at.searles.fractlang

import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.MetaOp
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val namedInstructions: Map<String, MetaOp>, private val definedParameters: Map<String, String>): SymbolTable {

    /**
     * Content is added to this map
     */
    private val parameterMap = LinkedHashMap<String, ExternNode>()
    private val activeParameterKeys = HashSet<String>()

    val activeParameters by lazy {
        parameterMap.filterKeys { activeParameterKeys.contains(it) }.values.sortedWith(TraceComparator()).map {
            it.id to ParameterEntry(it.id, it.description, it.isDefault, it.expr)
        }.toMap()
    }

    var defaultScale: DoubleArray? = null
        private set

    val defaultPalettes = ArrayList<PaletteData>()

    override fun get(trace: Trace, id: String): Node? {
        if(parameterMap.containsKey(id)) {
            activeParameterKeys.add(id)
            return parameterMap[id]
        }

        if(namedInstructions.containsKey(id)) {
            return namedInstructions.getValue(id).toNode(trace)
        }

        return null
    }

    override fun addExternValue(trace: Trace, name: String, description: String, expr: String) {
        if(parameterMap.containsKey(name)) {
            throw SemanticAnalysisException("extern $name already defined", trace)
        }

        val isDefault = !definedParameters.containsKey(name)

        val node = ExternNode(trace, name, description, isDefault,
            if(isDefault) expr else definedParameters.getValue(name))

        parameterMap[name] = node
    }

    override fun setScale(scaleArray: DoubleArray) {
        require(scaleArray.size == 6)

        defaultScale = scaleArray
    }

    override fun addPalette(paletteData: PaletteData) {
        defaultPalettes.add(paletteData)
    }

    class TraceComparator: Comparator<ExternNode> {
        override fun compare(n0: ExternNode, n1: ExternNode): Int {
            val cmp = n0.trace.start.compareTo(n1.trace.start)
            return if(cmp != 0) cmp else n0.trace.end.compareTo(n1.trace.end)
        }
    }
}