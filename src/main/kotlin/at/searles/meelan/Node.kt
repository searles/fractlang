package at.searles.meelan

import at.searles.parsing.ParserStream
import at.searles.parsingtools.SyntaxInfo

abstract class Node: SyntaxInfo, Op {
    protected constructor(stream: ParserStream) : super(stream) {

    }

    protected constructor(node: Node) : super(node) {
    }
}