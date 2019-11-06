package at.searles.meelan

import at.searles.parsing.Fold
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

val toInt = {s: CharSequence -> s.toString().toInt()}
val toHex = {s: CharSequence -> s.toString().toBigInteger(16).toInt()}
val toReal = {s: CharSequence -> s.toString().toDouble()}
val toIdString = {s: CharSequence -> s.toString()}

object toIntNode: Mapping<Int, Node> {
    override fun parse(stream: ParserStream, left: Int): Node? {
        return IntNode(stream, left)
    }

    override fun left(result: Node): Int? {
        return (result as? IntNode)?.value
    }
}

object toRealNode: Mapping<Double, Node> {
    override fun parse(stream: ParserStream, left: Double): Node? {
        return RealNode(stream, left)
    }

    override fun left(result: Node): Double? {
        return (result as? RealNode)?.value
    }
}

object toStringNode: Mapping<String, Node> {
    override fun parse(stream: ParserStream, left: String): Node? {
        return StringNode(stream, left)
    }

    override fun left(result: Node): String? {
        return (result as? StringNode)?.string
    }
}

object toIdNode: Mapping<String, Node> {
    override fun parse(stream: ParserStream, left: String): Node? {
        return IdNode(stream, left)
    }

    override fun left(result: Node): String? {
        return (result as? IdNode)?.id
    }
}

object toVectorNode: Mapping<List<Node>, Node> {
    override fun parse(stream: ParserStream, left: List<Node>): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun left(result: Node): List<Node>? {
        return null
    }
}

object toQualified: Fold<Node, String, Node> {
    override fun apply(stream: ParserStream, left: Node, right: String): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun leftInverse(result: Node): Node? {
        return null
    }

    override fun rightInverse(result: Node): String? {
        return null
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
        return App(stream, left, right)
    }

    override fun leftInverse(result: Node): Node? {
        return (result as? App)?.op as? Node
    }

    override fun rightInverse(result: Node): List<Node>? {
        return (result as? App)?.args
    }
}

object toBlock: Mapping<List<Node>, Node> {
    override fun parse(stream: ParserStream, left: List<Node>): Node? {
        return Block(stream, left)
    }

    override fun left(result: Node): List<Node>? {
        return (result as? Block)?.stmts
    }
}

private fun appArgOrNull(app: Node, op: Op, arity: Int, index: Int): Node? {
    if(app !is App
        || app.op != op
        || app.args.size != arity) return null

    return app.args[index]
}

fun toUnary(op: Op): Mapping<Node, Node> {
    return object: Mapping<Node, Node> {
        override fun parse(stream: ParserStream, left: Node): Node? {
            return App(stream, op, listOf(left))
        }

        override fun left(result: Node): Node? {
            return appArgOrNull(result, op, 1, 0)
        }
    }
}

fun toBinary(op: Op): Fold<Node, Node, Node> {
    return object: Fold<Node, Node, Node> {
        override fun apply(stream: ParserStream, left: Node, right: Node): Node {
            return App(stream, op, listOf(left, right))
        }

        override fun leftInverse(result: Node): Node? {
            return appArgOrNull(result, op, 2, 0)
        }

        override fun rightInverse(result: Node): Node? {
            return appArgOrNull(result, op, 2, 1)
        }
    }
}
