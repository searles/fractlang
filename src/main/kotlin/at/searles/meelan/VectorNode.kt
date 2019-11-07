package at.searles.meelan

import at.searles.parsing.ParserStream

class VectorNode(stream: ParserStream, val items: List<Node>) : Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
