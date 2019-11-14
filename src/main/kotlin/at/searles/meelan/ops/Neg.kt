package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Neg: BaseOp(
    Signature(BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
}