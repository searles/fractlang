package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Neg: BaseOp(
    Signature(BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx)
) {
}