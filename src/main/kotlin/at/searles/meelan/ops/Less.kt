package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Less: BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
}