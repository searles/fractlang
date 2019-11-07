package at.searles.meelan

import at.searles.parsing.ParserStream

class VarDecl(stream: ParserStream, val name: String, val typeName: String?, val init: Node?): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}