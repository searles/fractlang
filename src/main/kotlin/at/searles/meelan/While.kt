package at.searles.meelan

import at.searles.parsing.ParserStream

class While(stream: ParserStream, val condition: Node, val body: Node?): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}