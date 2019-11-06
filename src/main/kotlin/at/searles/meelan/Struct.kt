package at.searles.meelan

class Struct(val size: Int): Type {
    override fun byteCount(): Int {
        return size
    }

    override fun convert(node: Node): Node? {
        // effectively ban conversions
        return null
    }
}