package at.searles.fractlang

import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.MetaOp
import at.searles.fractlang.ops.Op
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val namedInstructions: Map<String, MetaOp>, private val parameters: Map<String, String>): SymbolTable {

    /**
     * Content is added to this map
     */
    private val parameterMap = LinkedHashMap<String, ExternNode>()

    private val activeExternNodesMap by lazy {
        parameterMap.values.sortedWith(TraceComparator()).map { it.id to it }.toMap()
    }

    // This one uses order of trace.
    val activeParameters: Map<String, String> by lazy {
        activeExternNodesMap.mapValues { it.value.expr }
    }

    val descriptionMap by lazy {
        activeExternNodesMap.mapValues { it.value.description }
    }

    var scale: DoubleArray? = null
        private set

    val palettes = ArrayList<PaletteData>()

    override fun get(trace: Trace, id: String): Node? {
        if(parameterMap.containsKey(id)) {
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

        val node = ExternNode(trace, name, description, parameters.getOrElse(name, {expr}))

        parameterMap[name] = node
    }

    override fun setScale(scaleArray: DoubleArray) {
        require(scaleArray.size == 6)

        scale = scaleArray
    }

    override fun addPalette(paletteData: PaletteData) {
        palettes.add(paletteData)
    }

    class TraceComparator: Comparator<ExternNode> {
        override fun compare(n0: ExternNode, n1: ExternNode): Int {
            val cmp = n0.trace.start.compareTo(n1.trace.start)
            return if(cmp != 0) cmp else n0.trace.end.compareTo(n1.trace.end)
        }
    }

    companion object {
        const val declarePalette = "declarePalette"
        const val declareScale = "declareScale"
    }
}