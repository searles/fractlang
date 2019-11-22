package at.searles.meelan.ops

import at.searles.meelan.nodes.App
import at.searles.meelan.nodes.Node
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.ConstValue
import at.searles.parsing.Trace

abstract class BaseOp(vararg val signatures: Signature) : Op {

    init {
        require(signatures.all { it.argTypes.size == signatures[0].argTypes.size })
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        // FIXME convertArgumets, use Optimizer

        // find correct type
        val signatureIndex = getSignatureIndex(args)

        if(signatureIndex == -1) {
            throw SemanticAnalysisException("could not apply $this to $args", trace)
        }

        if(args.all { it is ConstValue }) {
            return eval(trace, args)
        }

        return App(trace, this, args).apply {
            this.type = signatures[signatureIndex].returnType
        }
    }

    open fun parameterConfigurationCount() {
	}
	
	open fun getParameterConfiguration(index: Int): List<Pair<Type, Boolean>> {
	}

	open fun indexOfParameterConfiguration(List<Node>) { 
		
	}

    /**
     * Returns the index of the matching signature
     * @return -1 if there is no match.
     */
    /*open fun getSignatureIndex(args: List<Node>): Int {
        return signatures.indexOfFirst { it.matches(args) }
    }*/

    override fun toString(): String {
        return javaClass.simpleName
    }

    /*private fun countKindsPerSignature(): Int {
        return 1 shl signatures[0].argTypes.size - 1
    }

    open fun countKinds(): Int {
        return countKindsPerSignature() * signatures.size
    }

    open fun indexOf(args: List<Node>): Int {
        // for each signature add 2^|args|-1
        val sigIndex = getSignatureIndex(args)

        require(sigIndex >= 0)

        return sigIndex * countKindsPerSignature() + kindIndex(args)

    }

    private fun kindIndex(args: List<Node>): Int {
        val signature = signatures[0]

        var index = 0

        for(i in signature.argTypes.indices.reversed()) {
            index *= 2

            if(args[i] is ConstValue) {
                index += 1
            }
        }

        return index
    }

    open fun getIsConstArrayForIndex(kindIndex: Int): Array<Boolean> {
        val signature = signatures[0]

        var index = kindIndex

        val argIsConst = Array(signature.argTypes.size) { false }

        for(i in signature.argTypes.indices) {
            argIsConst[i] = (index % 2) == 1
            index /= 2
        }

        require(kindIndex == 0)

        return argIsConst
    }

    open fun getSignatureForIndex(index: Int): Signature {
        return signatures[index / countKindsPerSignature()]
    }*/
}
