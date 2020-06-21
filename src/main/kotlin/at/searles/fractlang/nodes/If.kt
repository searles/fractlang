package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import at.searles.parsing.Trace
class If(trace: Trace, val condition: Node, val thenBranch: Node): Node(trace) {
    init {
        type = BaseTypes.Unit
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "if($condition) $thenBranch"
    }

    object Creator: Mapping<Map<String, Node>, Node> {
        override fun parse(stream: ParserStream, input: Map<String, Node>): Node {
            return If(stream.createTrace(), input.getValue("condition"), input.getValue("then"))
        }

        override fun left(result: Node): Map<String, Node>? {
            return (result as? If)?.let {
                mapOf("condition" to it.condition, "then" to it.thenBranch)
            }
        }

        override fun toString(): String {
            return javaClass.simpleName
        }
    }

}