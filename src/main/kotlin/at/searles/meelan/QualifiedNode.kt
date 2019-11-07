package at.searles.meelan

import at.searles.parsing.ParserStream

class QualifiedNode(stream: ParserStream, val instance: Node, val qualifier: String) : Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
