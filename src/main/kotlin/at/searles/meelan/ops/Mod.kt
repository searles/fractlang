package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Mod: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int)
) {
}