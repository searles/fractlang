package at.searles.meelan

import at.searles.parsing.ParserStream

class IfElse(stream: ParserStream, val condition: Node, val thenBranch: Node, val elseBranch: Node): Node(stream) {
}