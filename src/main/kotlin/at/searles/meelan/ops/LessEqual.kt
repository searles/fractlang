package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object LessEqual: BaseOp(
    // FIXME Replace by only one comparison
    Signature(BaseTypes.Bool, BaseTypes.Integer, BaseTypes.Integer),
    Signature(BaseTypes.Bool, BaseTypes.Real, BaseTypes.Real)
) {
}