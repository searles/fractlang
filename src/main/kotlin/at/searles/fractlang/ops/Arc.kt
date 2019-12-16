package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.fractlang.semanticanalysis.Optimizer
import at.searles.parsing.Trace

object Arc: HasSpecialSyntax, StandardOp (
    Signature(BaseTypes.Real, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return if(args[0] is CplxNode)
            RealNode(trace, (args[0] as CplxNode).value.arc())
        else
            App(trace, this, args).apply {
                type = signatures[0].returnType
            }
    }
}