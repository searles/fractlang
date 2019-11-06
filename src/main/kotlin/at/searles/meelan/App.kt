package at.searles.meelan

import at.searles.parsing.ParserStream

class App(stream: ParserStream, val op: Op, val args: List<Node>): Node(stream) {
}