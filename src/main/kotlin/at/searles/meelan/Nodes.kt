package at.searles.meelan

import at.searles.meelan.ops.BaseOp
import at.searles.meelan.ops.HasSpecialSyntax
import at.searles.parsing.*

val toInt = {s: CharSequence -> s.toString().toInt()}
val toHex = {s: CharSequence -> s.toString().toBigInteger(16).toInt()}
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
        return (result as? StringNode)?.string
    }
}

object toIdNode: Mapping<String, Node> {
    override fun parse(stream: ParserStream, left: String): Node? {
        return IdNode(stream.createTrace(), left)
    }

    override fun left(result: Node): String? {
        return (result as? IdNode)?.id
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun left(result: String): CharSequence? {
        return null
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
        if(result !is App || result.head !is OpNode || result.head.op is HasSpecialSyntax) {
            return null
        }

        return result.head
    }

    override fun rightInverse(result: Node): List<Node>? {
        if(result !is App || result.head !is OpNode || result.head.op is HasSpecialSyntax) {
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

private fun appArgOrNull(app: Node, op: BaseOp, arity: Int, index: Int): Node? {
    if(app !is App
        || app.head !is OpNode
        || app.head.op != op
        || app.args.size != arity) return null

    return app.args[index]
}

fun toUnary(op: BaseOp): Mapping<Node, Node> {
    return object: Mapping<Node, Node> {
        override fun parse(stream: ParserStream, left: Node): Node? {
            return App(stream.createTrace(), op, listOf(left))
        }

        override fun left(result: Node): Node? {
            return appArgOrNull(result, op, 1, 0)
        }
    }
}

fun toBinary(op: BaseOp): Fold<Node, Node, Node> {
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

object toBool: Mapping<CharSequence, Node> {
    override fun parse(stream: ParserStream, left: CharSequence): Node? {
        return BoolNode(stream.createTrace(), left.toString().toBoolean())
    }

    override fun left(result: Node): CharSequence? {
        return (result as? BoolNode)?.value?.toString()
    }
}

object toNop: Initializer<Node> {
    override fun parse(stream: ParserStream): Node? {
        return Nop(stream.createTrace())
    }
}

object OpNodePrinter: Mapping<Node, Node> {
	override fun parse(stream: ParserStream, left: Node): Node? {
		return null
	}
	
	override fun left(result: Node): Node? {
		return (Node as? OpNode)?.let {
			IdNode(result.trace, op.toString())
		}
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

