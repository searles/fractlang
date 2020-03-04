package at.searles.fractlang

import at.searles.commons.color.Lab
import at.searles.commons.color.Palette
import at.searles.commons.color.Rgb
import at.searles.commons.math.Scale
import at.searles.commons.util.IntIntMap
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

    private val paletteEntries = ArrayList<PaletteEntry>()

    val palettes: List<PaletteEntry>
        get() {
            if(paletteEntries.isEmpty()) {
                return fallBackPalettes
            }

            return paletteEntries
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

    override fun setScale(scale: Scale) {
        defaultScale = scale
    }

    override fun addPalette(description: String, defaultPalette: Palette): Int {
        val entry = PaletteEntry(palettes.size, description, defaultPalette)
        val index = paletteEntries.size
        paletteEntries.add(entry)
        return index
    }

    class TraceComparator: Comparator<ExternNode> {
        override fun compare(n0: ExternNode, n1: ExternNode): Int {
            val cmp = n0.trace.start.compareTo(n1.trace.start)
            return if(cmp != 0) cmp else n0.trace.end.compareTo(n1.trace.end)
        }
    }

    companion object {
        val fallBackScale = Scale(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        val fallBackPalettes = listOf(
            PaletteEntry(0, "White (no palette defined in program)",
            Palette(1, 1, 0f, 0f, IntIntMap<Lab>().apply {
                set(0, 0, Rgb(0f, 0f, 0f).toLab())
            })))
    }
}