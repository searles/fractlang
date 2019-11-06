package at.searles.meelan

import at.searles.parsing.ParserStream

class VarDecl(stream: ParserStream, val name: String, val type: String?, val init: Node?): Node(stream) {
}