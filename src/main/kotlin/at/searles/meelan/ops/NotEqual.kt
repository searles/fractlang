package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object NotEqual: BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Integer, BaseTypes.Integer)
) {
}