package at.searles.fractlang

import at.searles.fractlang.ops.*

object FractlangUtils {
    val instructions = mapOf<String, Op>(
        "__add" to Add,
        "__sub" to Sub,
        "__mul" to Mul,
        "__div" to Div,
        "__mod" to Mod,
        "__pow" to Pow,
        "__neg" to Neg,
        "__abs" to Abs,
        "__less" to Less,
        "__lessequal" to LessEqual,
        "__equal" to Equal,
        "__notequal" to NotEqual,
        "__greaterequal" to GreaterEqual,
        "__greater" to Greater
    )
}