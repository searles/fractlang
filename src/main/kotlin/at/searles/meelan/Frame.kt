package at.searles.meelan

class Frame(block: Block): Node(block) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}