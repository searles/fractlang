package at.searles.fractlang.ops

import at.searles.demo.NumNode
import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace
import java.lang.IllegalArgumentException

/**
 * next(a, b) is equivalent to C++ (++a < b), thus a must not be const.
 */
object Next: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int)
) {
    override fun apply(trace: Trace, args: List<Node>): Node {
        if(args[0] !is IdNode) {
            throw SemanticAnalysisException(
                "Bad assignment",
                trace
            )
        }

        return evaluate(trace, args)
    }

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return App(trace, this, args).apply { type = BaseTypes.Bool }
    }

    override fun countArgKinds(): Int {
        return 2
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        return when(offset) {
            0 -> listOf(ArgKind(BaseTypes.Int, false), ArgKind(BaseTypes.Int, true))
            1 -> listOf(ArgKind(BaseTypes.Int, false), ArgKind(BaseTypes.Int, false))
            else -> error("unexpected since there are only two kinds.")
        }
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        return if(args[1] is IntNode) {
            0
        } else {
            1
        }
    }

    override fun getSignatureAt(offset: Int): Signature {
        return signatures[0]
    }
}