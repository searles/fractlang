package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.parsing.Trace

/**
 * Requires as input a layer
 */
object SetResult: BaseOp(Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Cplx, BaseTypes.Real)) {
    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return App(trace, this, args).apply {
            type = BaseTypes.Unit
        }
    }

    override fun countArgKinds(): Int {
        return 8
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        return listOf(
            ArgKind(BaseTypes.Int, offset and 1 == 1),
            ArgKind(BaseTypes.Cplx, offset and 2 == 2),
            ArgKind(BaseTypes.Real, offset and 4 == 4))
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        var offset = 0

        if(args[0] is IntNode) offset = offset or 1
        if(args[1] is CplxNode) offset = offset or 2
        if(args[2] is RealNode) offset = offset or 4

        return offset
    }

    override fun getSignatureAt(offset: Int): Signature {
        return signatures[0]
    }
}