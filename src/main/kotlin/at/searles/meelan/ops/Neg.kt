package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Neg: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
}