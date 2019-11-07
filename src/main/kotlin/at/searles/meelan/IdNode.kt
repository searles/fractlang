package at.searles.meelan

import at.searles.parsing.ParserStream

class IdNode(stream: ParserStream, val id: String): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}