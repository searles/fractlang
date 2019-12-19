package at.searles.fractlang

import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Op
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

class RootSymbolTable(private val instructions: Map<String, Op>, private val definedExternValues: Map<String, String>): SymbolTable {

    private val externValueMap = HashMap<String, ExternNode>()

    val externValues: Map<String, String>
        get() = externValueMap.mapValues { it.value.expr }

    var scale: DoubleArray? = null
        private set

    val palettes = ArrayList<PaletteData>()

    override fun get(trace: Trace, id: String): Node? {
        if(externValueMap.containsKey(id)) {
            return externValueMap[id]
        }

        if(instructions.containsKey(id)) {
            return instructions.getValue(id).toNode(trace)
        }

        if(id == declareScale) { // TODO Special ops. Not the best design...
            return OpNode(trace, DeclareScale())
        }

        if(id == declarePalette) {
            return OpNode(trace, DeclarePalette())
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

    private inner class DeclareScale: Op {
        // TODO Not a pretty design...
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

        // TODO override val usage: String = "declareScale(a, b, c, d, e, f)"
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

        // TODO override val usage: String = "declarePalette(name, width, height, [x, y, color], ...)"
    }

    class PaletteData(val name: String, val width: Int, val height: Int, val points: List<IntArray>)

    companion object {
        const val declarePalette = "declarePalette"
        const val declareScale = "declareScale"
    }
}