package at.searles.meelan.nodes

interface NumValue: ConstValue {
    fun isZero(): Boolean
    fun isOne(): Boolean
}