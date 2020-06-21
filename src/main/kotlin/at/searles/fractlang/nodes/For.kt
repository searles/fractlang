package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import at.searles.parsing.Trace

class For(trace: Trace, val name: Node, val range: Node, val body: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    object Creator: Mapping<Map<String, Node>, Node> {
        override fun parse(stream: ParserStream, input: Map<String, Node>): Node {
            return For(stream.createTrace(), input.getValue("name"), input.getValue("range"), input.getValue("body"))
        }

        override fun left(result: Node): Map<String, Node>? {
            return (result as? For)?.let {
                mapOf("name" to it.name, "range" to it.range, "body" to it.body)
            }
        }

        override fun toString(): String {
            return javaClass.simpleName
        }
    }

}