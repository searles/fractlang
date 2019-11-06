package at.searles.meelan

interface Type {
    fun convert(node: Node): Node?
    fun byteCount(): Int // 0 for Bool and Unit
}