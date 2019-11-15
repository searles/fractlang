package at.searles.meelan.ops

import at.searles.meelan.App
import at.searles.meelan.Node
import at.searles.meelan.SemanticAnalysisException
import at.searles.parsing.Trace

abstract class BaseOp(private vararg val signatures: Signature) : Op {
    open fun findSignature(arguments: List<Node>): Signature? {
        return signatures.firstOrNull{
            signature -> signature.argTypes.size == arguments.size
                && signature.argTypes.zip(arguments).all { it.first.canConvert(it.second) }
        }
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        // find correct type
        val signature = findSignature(args)
        if(signature != null) {
            return App(trace, this, args).apply {
                this.type = signature.returnType
            }
        } else {
            throw SemanticAnalysisException("could not determine type for $this", trace)
        }
    }
}