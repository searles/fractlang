package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.Optimizer
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Add: HasSpecialSyntax, StandardOp (
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return Optimizer.add(trace, args)
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