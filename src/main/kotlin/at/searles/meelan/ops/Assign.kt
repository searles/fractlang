package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.ConstValue
import at.searles.meelan.nodes.IdNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Assign: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int, BaseTypes.Int),
    Signature(BaseTypes.Unit, BaseTypes.Real, BaseTypes.Real),
    Signature(BaseTypes.Unit, BaseTypes.Cplx, BaseTypes.Cplx)
) {
    override fun getSignatureForIndex(offset: Int): Signature {
        return signatures[offset / 2]
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        if(args[0] !is IdNode) {
            throw SemanticAnalysisException("Bad assignment", trace)
        }

        return super.apply(trace, args)
    }

    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return app(trace, signature, args)
    }

    override fun countParameterConfigurations(): Int {
        return signatures.size * 2
    }

    /**
     * Returns a pair
     */
    override fun getParameterConfiguration(index: Int): List<ParameterConfig> {
        val signature = getSignatureForIndex(index)
        return listOf(ParameterConfig(signature.argTypes[0], false), ParameterConfig(signature.argTypes[1], index % 2 == 1))
    }

    override fun indexOfParameterConfiguration(args: List<Node>): Int {
        return 2 * getSignatureIndex(args) + if(args[1] is ConstValue) 1 else 0
    }

}