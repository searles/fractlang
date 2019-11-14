package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object And: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool, BaseTypes.Bool)
) {
}