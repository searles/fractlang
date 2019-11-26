package at.searles.meelan.ops

import at.searles.meelan.nodes.ConstValue
import at.searles.meelan.nodes.Node

/**
 * As opposed to SystemOps like jumps.
 */
abstract class StandardOp(vararg signatures: Signature): BaseOp(*signatures) {
    init {
        require(signatures.all { it.argTypes.size == signatures[0].argTypes.size })
    }

    private val countPerSignature = 1 shl signatures[0].argTypes.size - 1

    override fun countParameterConfigurations(): Int {
        return countPerSignature * signatures.size
    }

    override fun getParameterConfiguration(index: Int): List<ParameterConfig> {
        val parameterConfiguration = ArrayList<ParameterConfig>()

        val signature = getSignatureForIndex(index)
        var i = index % countPerSignature

        for(type in signature.argTypes) {
            parameterConfiguration.add(ParameterConfig(type, (i % 2) == 1))
            i /= 2
        }

        require(i == 0)

        return parameterConfiguration
    }

    override fun indexOfParameterConfiguration(args: List<Node>): Int {
        val sigIndex = getSignatureIndex(args)
        var index = getSignatureIndex(args) * countPerSignature

        for(i in signatures[sigIndex].argTypes.indices.reversed()) {
            index *= 2

            if(args[i] is ConstValue) {
                index += 1
            }
        }

        return index
    }

    override fun getSignatureForIndex(offset: Int): Signature {
        val index = offset / countPerSignature

        require(0 <= index && index < signatures.size) { "bad call: $this[$offset]" }

        return signatures[index]
    }
}