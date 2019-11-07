package at.searles.meelan

import at.searles.parsing.ParserStream

class ClassDecl(stream: ParserStream, val name: String, val parameters: List<Node>, val body: Node): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}