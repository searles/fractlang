package at.searles.fractlang

import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Op
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val namedInstructions: Map<String, Op>, private val parameters: Map<String, String>): SymbolTable {

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

        if(id == declareScale) { // TODO Special ops. Not the best design...
            return OpNode(trace, DeclareScale())
        }

        if(id == declarePalette) {
            return OpNode(trace, DeclarePalette())
        }

        return null
    }

    override fun addExternValue(trace: Trace, name: String, description: String, expr: String) {
        if(parameterMap.containsKey(name)) {
            throw SemanticAnalysisException("extern $name already defined", trace)
        }

        val node = ExternNode(trace, name, description, parameters.getOrElse(name, {expr}))

        parameterMap[name] = node

        // TODO sort by trace.

    }

    private inner class DeclareScale: Op {
        override fun apply(trace: Trace, args: List<Node>): Node {
            if(scale != null) {
                throw SemanticAnalysisException("scale already declared", trace)
            }

            if(args.size != 6) {
                throw SemanticAnalysisException("declareScale must have 6 parameters", trace)
            }

            scale =  args.map { (BaseTypes.Real.convert(it) as RealNode).value }.toDoubleArray()

            return Nop(trace)
        }
    }

    private inner class DeclarePalette: Op {
        private fun toColorPoint(node: Node, width: Int, height: Int): IntArray {
            if(node !is VectorNode || node.items.size != 3) {
                throw SemanticAnalysisException("not a vector", node.trace)
            }

            node.items.forEach {
                if(it !is IntNode) {
                    throw SemanticAnalysisException("not an integer", it.trace)
                }
            }

            val pt = IntArray(3) {(node.items[it] as IntNode).value}

            if(pt[0] < 0 || width <= pt[0]) {
                throw SemanticAnalysisException("Not in range of palette", node.items[0].trace)
            }

            if(pt[1] < 0 || height <= pt[1]) {
                throw SemanticAnalysisException("Not in range of palette", node.items[0].trace)
            }

            return pt
        }

        override fun apply(trace: Trace, args: List<Node>): Node {
            val name = (args[0] as? StringNode)?.value ?: throw SemanticAnalysisException("arg0 must be a String", args[0].trace)
            val width = (args[1] as? IntNode)?.value ?: throw SemanticAnalysisException("arg1 must be an Int", args[1].trace)
            val height = (args[2] as? IntNode)?.value ?: throw SemanticAnalysisException("arg2 must be an Int", args[2].trace)

            if(width <= 0) {
                throw SemanticAnalysisException("width must be > 0", trace)
            }

            if(height <= 0) {
                throw SemanticAnalysisException("height must be > 0", trace)
            }

            val points = args.drop(3).map { toColorPoint(it, width, height) }

            if(points.isEmpty()) {
                throw SemanticAnalysisException("Palette must have at least one color point", trace)
            }

            palettes.add(PaletteData(name, width, height, points))

            return Nop(trace)
        }
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