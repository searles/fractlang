package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Less: BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
}