package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace

object Cons: HasSpecialSyntax, StandardOp(
    Signature(BaseTypes.Cplx, BaseTypes.Real, BaseTypes.Real)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args.all { it is NumValue }) {
            return CplxNode(trace, Cplx((args[0] as RealNode).value, (args[1] as RealNode).value))
        }

        return app(trace, args)
    }
}