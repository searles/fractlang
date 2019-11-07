package at.searles.meelan

import at.searles.parsing.ParserStream

class StringNode(stream: ParserStream, val string: String): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}