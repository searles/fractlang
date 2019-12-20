package at.searles.fractlang.parsing

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Type
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.Op
import at.searles.fractlang.ops.HasSpecialSyntax
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.*
import at.searles.regexparser.CodePointStream
import at.searles.regexparser.EscStringParser
import at.searles.regexparser.RegexParserException

val toInt = {s: CharSequence -> s.toString().toInt()}
val toHex = {s: CharSequence -> s.substring(1).toBigInteger(16).toInt()}
val toReal = {s: CharSequence -> s.toString().toDouble()}
val toIdString = {s: CharSequence -> s.toString()}

object toIntNode: Mapping<Int, Node> {
    override fun parse(stream: ParserStream, left: Int): Node? {
        return IntNode(stream.createTrace(), left)
    }

    override fun left(result: Node): Int? {
        return (result as? IntNode)?.value
    }
}

object toRealNode: Mapping<Double, Node> {
    override fun parse(stream: ParserStream, left: Double): Node? {
        return RealNode(stream.createTrace(), left)
    }

    override fun left(result: Node): Double? {
        return (result as? RealNode)?.value
    }
}

object toStringNode: Mapping<String, Node> {
    override fun parse(stream: ParserStream, left: String): Node? {
        return StringNode(stream.createTrace(), left)
    }

    override fun left(result: Node): String? {
        return (result as? StringNode)?.value
    }
}

object toIdNode: Mapping<String, Node> {
    override fun parse(stream: ParserStream, left: String): Node? {
        return IdNode(stream.createTrace(), left)
    }

    override fun left(result: Node): String? {
        // also covers ops that were already converted.
        return (result as? IdNode)?.id
            ?: (result as? OpNode)?.op?.toString()
    }
}

object toVectorNode: Mapping<List<Node>, Node> {
    override fun parse(stream: ParserStream, left: List<Node>): Node? {
        return VectorNode(stream.createTrace(), left)
    }

    override fun left(result: Node): List<Node>? {
        return (result as? VectorNode)?.items
    }
}

object toQualified: Fold<Node, String, Node> {
    override fun apply(stream: ParserStream, left: Node, right: String): Node {
        return QualifiedNode(stream.createTrace(), left, right)
    }

    override fun leftInverse(result: Node): Node? {
        return (result as? QualifiedNode)?.instance
    }

    override fun rightInverse(result: Node): String? {
        return (result as? QualifiedNode)?.qualifier
    }
}

object toEscString: Mapping<CharSequence, String> {
    override fun parse(stream: ParserStream, left: CharSequence): String? {
        return try {
            EscStringParser.parse(CodePointStream(left.toString()))
        } catch(e: RegexParserException) {
            null
        }
    }

    override fun left(result: String): CharSequence? {
        return EscStringParser.unparse(result)
    }
}

object listApply: Fold<List<Node>, Node, List<Node>> {
    // for (x+1) 5
    override fun apply(stream: ParserStream, left: List<Node>, right: Node): List<Node>? {
        // sin (x+1) y = sin ((x+1)*y)
        // max (x,y) z is an error.

        if(left.size != 1) {
            return null
        }

        return listOf(toApp.apply(stream, left.first(), listOf(right)))
    }

    // no inverse. Other methods will take care of that.
}

object toApp: Fold<Node, List<Node>, Node> {
    override fun apply(stream: ParserStream, left: Node, right: List<Node>): Node {
        return App(stream.createTrace(), left, right)
    }

    override fun leftInverse(result: Node): Node? {
        if(result !is App) {
            return null
        }

        if(result.head is OpNode && result.head.op is HasSpecialSyntax) {
            return null
        }

        return result.head
    }

    override fun rightInverse(result: Node): List<Node>? {
        if(result !is App) {
            return null
        }

        if(result.head is OpNode && result.head.op is HasSpecialSyntax) {
            return null
        }

        return result.args
    }
}

object toBlock: Mapping<List<Node>, Node> {
    override fun parse(stream: ParserStream, left: List<Node>): Node? {
        return Block(stream.createTrace(), left)
    }

    override fun left(result: Node): List<Node>? {
        return (result as? Block)?.stmts
    }
}

private fun appArgOrNull(app: Node, op: Op, arity: Int, index: Int): Node? {
    if(app !is App
        || app.head !is OpNode
        || app.head.op != op
        || app.args.size != arity) return null

    return app.args[index]
}

fun toUnary(op: Op): Mapping<Node, Node> {
    return object: Mapping<Node, Node> {
        override fun parse(stream: ParserStream, left: Node): Node? {
            return App(stream.createTrace(), op, listOf(left))
        }

        override fun left(result: Node): Node? {
            return appArgOrNull(result, op, 1, 0)
        }
    }
}

object toAssignment: Fold<Node, Node, Node> {
    override fun apply(stream: ParserStream, left: Node, right: Node): Node {
        return Assignment(stream.createTrace(), left, right)
    }

    override fun leftInverse(result: Node): Node? {
        return (result as? Assignment)?.lhs
    }

    override fun rightInverse(result: Node): Node? {
        return (result as? Assignment)?.rhs
    }
}

fun toBinary(op: Op): Fold<Node, Node, Node> {
    return object: Fold<Node, Node, Node> {
        override fun apply(stream: ParserStream, left: Node, right: Node): Node {
            return App(stream.createTrace(), op, listOf(left, right))
        }

        override fun leftInverse(result: Node): Node? {
            return appArgOrNull(result, op, 2, 0)
        }

        override fun rightInverse(result: Node): Node? {
            return appArgOrNull(result, op, 2, 1)
        }
    }
}

fun stringToType(trace: Trace, typeName: String): Type {
    return BaseTypes.values().firstOrNull { it.name == typeName }
        ?: throw SemanticAnalysisException("no such type", trace)
}

object ToType: Mapping<String, Type> {
    override fun parse(stream: ParserStream, left: String): Type? {
        return stringToType(stream.createTrace(), left)
    }

    override fun left(result: Type): String? {
        return result.toString()
    }
}

fun toBool(value: Boolean): Initializer<Boolean> {
    return object: Initializer<Boolean> {
        override fun parse(stream: ParserStream): Boolean? {
            return value
        }

        override fun consume(t: Boolean): Boolean {
            return t == value
        }
    }
}

object toBoolNode: Mapping<Boolean, Node> {
    override fun parse(stream: ParserStream, left: Boolean): Node? {
        return BoolNode(stream.createTrace(), left)
    }

    override fun left(result: Node): Boolean? {
        return (result as? BoolNode)?.value
    }
}

object createNop: Initializer<Node> {
    override fun parse(stream: ParserStream): Node? {
        return Nop(stream.createTrace())
    }

    override fun consume(t: Node?): Boolean {
        return t is Nop
    }
}


/**
 * Identity in parse-direction. In left-direction, this
 * mapping passes blocks through so that no semicolons
 * are added after blocks.
 */
object SkipSemicolon: Mapping<Node, Node> {
    override fun parse(stream: ParserStream?, left: Node): Node? {
        return left
    }

    override fun left(result: Node): Node? {
        if(result is Block || result is ClassDecl || (result is FunDecl && result.body is Block)) {
            return result
        }

        return null
    }
}

