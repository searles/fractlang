package at.searles.meelan

import at.searles.parsing.ParserStream

class Block(stream: ParserStream, val stmts: List<Node>): Node(stream) {
}