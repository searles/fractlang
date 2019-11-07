package at.searles.meelan

import at.searles.parsing.ParserStream
import at.searles.parsingtools.SyntaxInfo

abstract class Node: SyntaxInfo, Op {
    protected constructor(stream: ParserStream) : super(stream)
    protected constructor(node: Node) : super(node)

    var type: Type? = null // null means not assigned yet.

    abstract fun <T> accept(visitor: Visitor<T>): T
}