package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.IdNode
import at.searles.meelan.Node

object Assign: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Unit, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Unit, BaseTypes.Cplx, BaseTypes.Cplx)
) {
}