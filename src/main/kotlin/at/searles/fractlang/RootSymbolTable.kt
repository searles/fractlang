package at.searles.fractlang

import at.searles.commons.math.Scale
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

    var defaultScale: Scale = fallBackScale
        private set

    private val defaultPaletteData = ArrayList<PaletteData>()

    val defaultPalettes: List<PaletteData>
        get() {
            if(defaultPaletteData.isEmpty()) {
                return fallBackPalettes
            }

            return defaultPaletteData
        }

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

        defaultScale = toScale(scaleArray)
    }

    private fun toScale(array: DoubleArray): Scale {
        return Scale(array[0], array[1], array[2], array[3], array[4], array[5])
    }

    override fun addPalette(paletteData: PaletteData) {
        defaultPaletteData.add(paletteData)
    }

    class TraceComparator: Comparator<ExternNode> {
        override fun compare(n0: ExternNode, n1: ExternNode): Int {
            val cmp = n0.trace.start.compareTo(n1.trace.start)
            return if(cmp != 0) cmp else n0.trace.end.compareTo(n1.trace.end)
        }
    }

    companion object {
        val fallBackScale = Scale(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        val fallBackPalettes = listOf(PaletteData("White (no palette defined in program)", 1, 1, listOf(intArrayOf(0, 0, -1))))
    }
}