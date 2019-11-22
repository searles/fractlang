package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.IdNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Assign: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Unit, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Unit, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun apply(trace: Trace, args: List<Node>): Node {
        if(args[0] !is IdNode) {
            throw SemanticAnalysisException("Bad assignment", trace)
        }

        return super.apply(trace, args)
    }

    override fun eval(trace: Trace, args: List<Node>): Node {
        error("matching for Assign causes a bug.")
    }

    override fun countKinds(): Int {
        return signatures.size * 2
    }

    override fun indexOf(args: List<Node>): Int {
        return super.indexOf(args)
    }

    override fun getIsConstArrayForIndex(kindIndex: Int): Array<Boolean> {
        return booleanArrayOf(false,  true)
    }

    override fun getSignatureForIndex(index: Int): Signature {
        return super.getSignatureForIndex(index)
    }

    // FIXME override stuff!

}