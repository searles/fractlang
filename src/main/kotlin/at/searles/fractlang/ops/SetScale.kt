package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.Node
import at.searles.fractlang.nodes.Nop
import at.searles.fractlang.nodes.RealNode
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.semanticanalysis.SemanticAnalysisVisitor
import at.searles.parsing.Trace

object SetScale: MetaOp {
    override fun inlineApply(trace: Trace, args: List<Node>, visitor: SemanticAnalysisVisitor): Node {
        if(args.size != 6) {
            throw SemanticAnalysisException("declareScale must have 6 parameters", trace)
        }

        val inlinedArgs = args.map { it.accept(visitor) }

        val scaleArray =  inlinedArgs.map { (BaseTypes.Real.convert(it) as RealNode).value }.toDoubleArray()

        visitor.table.setScale(scaleArray)

        return Nop(trace)
    }
}