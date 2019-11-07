package at.searles.meelan

import at.searles.parsing.ParserStream

class Block(stream: ParserStream, val stmts: List<Node>): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}