package at.searles.fractlang.ops

import at.searles.fractlang.nodes.Node
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import at.searles.fractlang.Type
import at.searles.fractlang.nodes.App
import at.searles.parsing.Trace

/**
 * These operations are directly executed in the Vm.
 */
abstract class BaseOp(vararg val signatures: Signature) : Op {

    override fun apply(trace: Trace, args: List<Node>): Node {
        val signature = signatures.find { it.matches(args) } ?:
        throw SemanticAnalysisException(
            "no matching signature",
            trace
        )

        val typedArgs = signature.convertArguments(args)

        return evaluate(trace, typedArgs)
    }

    protected fun app(trace: Trace, args: List<Node>): Node {
        val returnType = signatures.find { it.matches(args) }?.returnType

        require(returnType != null) { "optimizer returned weird arguments for $this: $args" }

        return App(trace, this, args).apply { type = returnType }
    }

    /**
     * Returns the index of the matching signature
     * @return -1 if there is no match.
     */
    fun getSignatureIndex(args: List<Node>): Int {
        return signatures.indexOfFirst { it.matches(args) }
    }

    /**
     * Creates a node representing an application of this to args.
     */
    abstract fun evaluate(trace: Trace, args: List<Node>): Node

    /**
     * Returns the count of possible argument combinations consisting
     * of Type/IsConst
     */
    abstract fun countArgKinds(): Int

    /**
     * Returns the concrete parameter configuration for the given offset.
     */
	abstract fun getArgKindAt(offset: Int): List<ArgKind>

    /**
     * Inverse of getParameterConfiguration
     */
	abstract fun getArgKindOffset(args: List<Node>): Int

    /**
     * Returns the signature that is used for the parameter configuration
     * of the given index.
     */
    abstract fun getSignatureAt(offset: Int): Signature

    override fun toString(): String {
        return javaClass.simpleName
    }

    data class ArgKind(val type: Type, val isConst: Boolean)
}
