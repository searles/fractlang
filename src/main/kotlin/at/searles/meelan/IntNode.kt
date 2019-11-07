package at.searles.meelan

import at.searles.parsing.ParserStream

class IntNode(stream: ParserStream, val value: Int) : Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
