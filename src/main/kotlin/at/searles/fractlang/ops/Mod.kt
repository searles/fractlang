package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.App
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Mod: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Int, BaseTypes.Int, BaseTypes.Int)
) {
    private fun imod(a: Int, b: Int): Int {
        val m = a % b
        return if(m < 0) m + b else  m
    }

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        if(args[0] is IntNode && args[1] is IntNode) {
            return IntNode(trace, imod((args[0] as IntNode).value, (args[1] as IntNode).value))
        }

        return App(trace, this, args).apply { type = BaseTypes.Int }
    }

    override fun countArgKinds(): Int {
        return 3
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        return when(offset) {
            0 -> listOf(ArgKind(BaseTypes.Int, true), ArgKind(BaseTypes.Int, false))
            1 -> listOf(ArgKind(BaseTypes.Int, false), ArgKind(BaseTypes.Int, true))
            2 -> listOf(ArgKind(BaseTypes.Int, false), ArgKind(BaseTypes.Int, false))
            else -> error("out of bounds")
        }
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        if(args[0] is IntNode) {
            return 0
        }

        if(args[1] is IntNode) {
            return 1
        }

        return 2
    }

    override fun getSignatureAt(offset: Int): Signature {
        return signatures[0]
    }
}