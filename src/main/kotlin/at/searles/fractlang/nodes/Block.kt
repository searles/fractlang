package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import at.searles.parsing.Trace
class Block(trace: Trace, val stmts: List<Node>): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "{${stmts.joinToString("; ")}}"
    }

    object Creator: Mapping<List<Node>, Node> {
        override fun parse(stream: ParserStream, input: List<Node>): Node {
            return Block(stream.toTrace(), input)
        }

        override fun left(result: Node): List<Node>? {
            return (result as? Block)?.stmts
        }

        override fun toString(): String {
            return "{block}"
        }
    }


}