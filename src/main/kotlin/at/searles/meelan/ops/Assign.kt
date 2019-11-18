package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Assign: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Unit, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Unit, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        throw IllegalArgumentException()
    }

    override fun isLValueOnly(argIndex: Int): Boolean {
        return argIndex == 0
    }
}