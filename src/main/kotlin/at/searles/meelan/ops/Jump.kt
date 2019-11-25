package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.parsing.Trace

object Jump: HasSpecialSyntax, BaseOp(
    Signature(BaseTypes.Unit, BaseTypes.Int)
) {
    override fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node {
        return app(trace, signature, args)
    }

    override fun countParameterConfigurations(): Int {
        return 1
    }

    override fun getParameterConfiguration(index: Int): List<ParameterConfig> {
        return listOf(ParameterConfig(BaseTypes.Int, true))
    }

    override fun indexOfParameterConfiguration(args: List<Node>): Int {
        if(args[0] !is IntNode) {
            return -1
        }

        return 0
    }

    override fun getSignatureForIndex(offset: Int): Signature {
        return signatures[0]
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        throw SemanticAnalysisException("cannot apply jump at this stage", trace)
    }

    // TODO
}