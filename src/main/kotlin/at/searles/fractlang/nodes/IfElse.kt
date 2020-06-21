package at.searles.fractlang.nodes

import at.searles.fractlang.Visitor
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import at.searles.parsing.Trace
class IfElse(trace: Trace, val condition: Node, val thenBranch: Node, val elseBranch: Node): Node(trace) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "if($condition) $thenBranch else $elseBranch"
    }

    object Creator: Mapping<Map<String, Node>, Node> {
        override fun parse(stream: ParserStream, input: Map<String, Node>): Node {
            return IfElse(stream.createTrace(), input.getValue("condition"), input.getValue("then"), input.getValue("else"))
        }

        override fun left(result: Node): Map<String, Node>? {
            return (result as? IfElse)?.let {
                mapOf("condition" to it.condition, "then" to it.thenBranch, "else" to it.elseBranch)
            }
        }

        override fun toString(): String {
            return javaClass.simpleName
        }
    }

}