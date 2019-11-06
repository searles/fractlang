package at.searles.meelan

import at.searles.parsing.ParserStream

class If(stream: ParserStream, val condition: Node, val thenBranch: Node): Node(stream) {
}