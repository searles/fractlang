package at.searles.meelan.ops

import at.searles.meelan.BaseTypes
import at.searles.meelan.nodes.Node
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.Type
import at.searles.meelan.nodes.App
import at.searles.parsing.Trace

abstract class BaseOp(vararg val signatures: Signature) : Op {

    protected fun app(trace: Trace, signature: Signature, args: List<Node>): Node {
        return App(trace, this, args).apply { type = signature.returnType }
    }

    /**
     * Returns the index of the matching signature
     * @return -1 if there is no match.
     */
    fun getSignatureIndex(args: List<Node>): Int {
        return signatures.indexOfFirst { it.matches(args) }
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        val signature = signatures.find { it.matches(args) } ?:
            throw SemanticAnalysisException("no matching signature", trace)

        val typedArgs = signature.convertArguments(args)

        return evaluate(trace, signature, typedArgs)

    }

    abstract fun evaluate(trace: Trace, signature: Signature, args: List<Node>): Node

    abstract fun countParameterConfigurations(): Int

	abstract fun getParameterConfiguration(index: Int): List<ParameterConfig>

	abstract fun indexOfParameterConfiguration(args: List<Node>): Int

    abstract fun getSignatureForIndex(offset: Int): Signature

    override fun toString(): String {
        return javaClass.simpleName
    }

    data class ParameterConfig(val type: Type, val isConst: Boolean)
}
