package at.searles.meelan

import at.searles.parsing.ParserStream

class RealNode(stream: ParserStream, val value: Double) : Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
