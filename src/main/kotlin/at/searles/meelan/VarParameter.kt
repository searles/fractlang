package at.searles.meelan

import at.searles.parsing.ParserStream

class VarParameter(stream: ParserStream,  val name: String, val typeName: String?): Node(stream) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}