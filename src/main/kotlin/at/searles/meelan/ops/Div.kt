package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Div: BaseOp(
    Signature(BaseTypes.Integer, BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
}