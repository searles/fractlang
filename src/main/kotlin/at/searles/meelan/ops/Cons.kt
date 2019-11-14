package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Cons: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Cplx, BaseTypes.Real, BaseTypes.Real)
) {
}