package at.searles.meelan.ops

import at.searles.commons.math.Cplx
import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.parsing.Trace

object Add: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun eval(trace: Trace, args: List<Node>): Node {
        return when(val arg0 = args[0]) {
            is IntNode -> IntNode(trace, arg0.value + (args[1] as IntNode).value)
            is RealNode -> RealNode(trace, arg0.value + (args[1] as RealNode).value)
            is CplxNode -> CplxNode(trace, Cplx().add(arg0.value, (args[1] as CplxNode).value))
            else -> throw IllegalArgumentException()
        }
    }

    // TODO
    // 1. eval for NumNodes
    // 2. generate code for all cases

    // For 2. add
    // 'lvalue(type)', 'rvalue(type)
    // and for each of this, split off varieties.
    // lvalue.getKinds() = Var(type)
    // rvalue.getKinds() = Var(type)/Const(type)
}