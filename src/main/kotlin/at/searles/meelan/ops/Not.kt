package at.searles.meelan.ops

import at.searles.meelan.BaseTypes

object Not: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Bool)
) {
}