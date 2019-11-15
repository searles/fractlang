package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object ToReal: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Real, BaseTypes.Int)
) {
}