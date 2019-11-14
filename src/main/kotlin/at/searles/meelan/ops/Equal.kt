package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Equal: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int)
) {
}