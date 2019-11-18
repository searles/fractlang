package at.searles.meelan.nodes

/**
 * Marker interface to indicate that this represents a constant value
 */
interface ConstValue {
    fun isZero(): Boolean
    fun isOne(): Boolean
}
