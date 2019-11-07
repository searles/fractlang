package at.searles.meelan

import at.searles.parsing.ParserStream

class DefDecl(stream: ParserStream): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}