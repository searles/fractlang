package at.searles.meelan

import at.searles.parsing.ParserStream

class For(stream: ParserStream, val name: String, val range: Node, val body: Node): Node(stream) {
}