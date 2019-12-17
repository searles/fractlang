package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace
import kotlin.math.ln

object Log: HasSpecialSyntax, StandardOp (1,
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return when {
            args[0] is RealNode -> RealNode(trace, ln((args[0] as RealNode).value))
            args[0] is CplxNode -> CplxNode(trace, Cplx().log((args[0] as CplxNode).value))
            else -> App(trace, this, args).apply {
                type = signatures[0].returnType
            }
        }
    }
}