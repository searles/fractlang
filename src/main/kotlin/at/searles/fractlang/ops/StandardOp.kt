package at.searles.fractlang.ops

import at.searles.fractlang.nodes.ConstValue
import at.searles.fractlang.nodes.Node

/**
 * As opposed to SystemOps like jumps.
 */
abstract class StandardOp(vararg signatures: Signature): BaseOp(*signatures) {
    init {
        require(signatures.all { it.argTypes.size == signatures[0].argTypes.size })
    }

    private val countConfigPerSignature = 1 shl signatures[0].argTypes.size - 1

    override fun countArgKinds(): Int {
        return countConfigPerSignature * signatures.size
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        val parameterConfiguration = ArrayList<ArgKind>()

        val signature = getSignatureAt(offset)
        var i = offset % countConfigPerSignature

        for(type in signature.argTypes) {
            parameterConfiguration.add(ArgKind(type, (i % 2) == 1))
            i /= 2
        }

        require(i == 0)

        return parameterConfiguration
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        val sigIndex = getSignatureIndex(args)
        var index = 0

        for(i in signatures[sigIndex].argTypes.indices.reversed()) {
            index *= 2

            if(args[i] is ConstValue) {
                index += 1
            }
        }

        index += sigIndex * countConfigPerSignature

        return index
    }

    override fun getSignatureAt(offset: Int): Signature {
        val index = offset / countConfigPerSignature

        require(0 <= index && index < signatures.size) { "bad call: $this[$offset]" }

        return signatures[index]
    }
}