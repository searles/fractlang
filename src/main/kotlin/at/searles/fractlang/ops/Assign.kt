package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.nodes.ConstValue
import at.searles.fractlang.nodes.IdNode
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Assign: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Unit, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Unit, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun getSignatureAt(offset: Int): Signature {
        return signatures[offset / 2]
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        if(args[0] !is IdNode) {
            throw SemanticAnalysisException(
                "Bad assignment",
                trace
            )
        }

        return super.apply(trace, args)
    }

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return createTypedApp(trace, args)
    }

    override fun countArgKinds(): Int {
        return signatures.size * 2
    }

    /**
     * Returns a pair
     */
    override fun getArgKindAt(offset: Int): List<ArgKind> {
        val signature = getSignatureAt(offset)
        return listOf(ArgKind(signature.argTypes[0], false), ArgKind(signature.argTypes[1], offset % 2 == 1))
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        return 2 * getSignatureIndex(args) + if(args[1] is ConstValue) 1 else 0
    }

}