package at.searles.fractlang.ops

import at.searles.commons.math.Cplx
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace

object Cons: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Cplx, BaseTypes.Real, BaseTypes.Real)
) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args.all { it is NumValue }) {
            return CplxNode(trace, Cplx((args[0] as RealNode).value, (args[1] as RealNode).value))
        }

        return createTypedApp(trace, args)
    }

    override fun countArgKinds(): Int {
        return 3
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        return when(offset) {
            0 -> listOf(ArgKind(BaseTypes.Real, true), ArgKind(BaseTypes.Real, false))
            1 -> listOf(ArgKind(BaseTypes.Real, false), ArgKind(BaseTypes.Real, true))
            2 -> listOf(ArgKind(BaseTypes.Real, false), ArgKind(BaseTypes.Real, false))
            else -> error("out of bounds")
        }
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        if(args[0] is RealNode) {
            return 0
        }

        if(args[1] is RealNode) {
            return 1
        }

        return 2
    }

    override fun getSignatureAt(offset: Int): Signature {
        return signatures[0]
    }
}