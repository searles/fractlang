package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Greater: BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
}