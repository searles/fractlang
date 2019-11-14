package at.searles.meelan

interface Type {
    fun canConvert(node: Node): Boolean
    fun convert(node: Node): Node
    fun byteSize(): Int // 0 for Bool and Unit
    fun commonType(type: Type): Type?
}