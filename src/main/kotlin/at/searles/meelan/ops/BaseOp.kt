package at.searles.meelan.ops

import at.searles.meelan.Instruction
import at.searles.meelan.Node
import at.searles.parsing.Trace

abstract class BaseOp(vararg val signatures: Signature) {
    fun findSignature(arguments: List<Node>): Signature? {
        return signatures.firstOrNull{
            signature -> signature.argTypes.size == arguments.size
                && signature.argTypes.zip(arguments).all { it.first.canConvert(it.second) }
        }
    }

    fun apply(trace: Trace, args: List<Node>): Node {
        return Instruction(trace, this, *args.toTypedArray())
    }
}