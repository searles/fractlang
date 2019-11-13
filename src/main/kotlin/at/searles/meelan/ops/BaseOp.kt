package at.searles.meelan.ops

import at.searles.meelan.App
import at.searles.meelan.Node
import at.searles.parsing.Trace

abstract class BaseOp(private vararg val signatures: Signature) : Op {
    fun findSignature(arguments: List<Node>): Signature? {
        return signatures.firstOrNull{
            signature -> signature.argTypes.size == arguments.size
                && signature.argTypes.zip(arguments).all { it.first.canConvert(it.second) }
        }
    }

    fun apply(trace: Trace, args: List<Node>): Node {
        return App(trace, this, args)
    }
}