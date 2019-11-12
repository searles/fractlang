package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Pow: BaseOp(
    Signature(BaseTypes.Integer, BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Integer),
    Signature(BaseTypes.Real, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Integer),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Real),
    Signature(BaseTypes.Cplx, BaseTypes.Cplx, BaseTypes.Cplx)
) {
}