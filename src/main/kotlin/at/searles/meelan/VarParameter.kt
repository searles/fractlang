package at.searles.meelan

import at.searles.parsing.ParserStream

class VarParameter(stream: ParserStream,  val name: String, val type: String?): Node(stream) {
}