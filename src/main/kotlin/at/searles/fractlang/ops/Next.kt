package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.*
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.parsing.Trace

/**
 * next(a, b) is equivalent to C++ (++b < a), thus a must not be const.
 */
object Next: HasSpecialSyntax, StandardOp(2,
    Signature(BaseTypes.Bool, BaseTypes.Int, BaseTypes.Int)
) {
    override fun apply(trace: Trace, args: List<Node>): Node {
        if(args[1] !is IdNode) {
            throw SemanticAnalysisException(
                "Bad assignment",
                trace
            )
        }

        return evaluate(trace, args)
    }

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return createApp(trace, args)
    }
}