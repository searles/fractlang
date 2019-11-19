package at.searles.meelan.ops

import at.searles.meelan.nodes.App
import at.searles.meelan.nodes.Node
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.ConstValue
import at.searles.meelan.nodes.IdNode
import at.searles.parsing.Trace

abstract class BaseOp(private vararg val signatures: Signature) : Op {
    open fun findSignature(trace: Trace, arguments: List<Node>): Signature {
        // check L-value
        arguments.forEachIndexed { index, arg ->
            if(isLValueOnly(index) && arg !is IdNode) {
                throw SemanticAnalysisException("expression expects a variable", trace)
            }
        }

        return signatures.firstOrNull{
            signature ->
            signature.argTypes.size == arguments.size
                && signature.argTypes.zip(arguments).all { it.first.canConvert(it.second) }}
            ?:
                throw SemanticAnalysisException("not applicable to types ${arguments.map { it.type } }", trace)

    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        // find correct type
        val signature = findSignature(trace, args)

        if(args.all { it is ConstValue }) {
            return eval(trace, args)
        }

        return App(trace, this, args).apply {
            this.type = signature.returnType
        }
    }

    open fun isLValueOnly(argIndex: Int): Boolean {
        return false
    }

    abstract fun eval(trace: Trace, args: List<Node>): Node

    override fun toString(): String {
        return javaClass.simpleName
    }

    fun countArgKinds(signatureIndex: Int): Int {
        val signature = signatures[signatureIndex]

        var count = 1
        var hasLValue = false

        for(i in signature.argTypes.indices) {
            if(!isLValueOnly(i)) {
                count *= 2
            } else {
                hasLValue = true
            }
        }

        return if(!hasLValue) count - 1 else count
    }

    fun getArgKind(signatureIndex: Int, kindIndex: Int): Array<Boolean> {
        val signature = signatures[signatureIndex]

        var index = kindIndex

        val argIsConst = Array(signature.argTypes.size) { false }

        for(i in signature.argTypes.indices) {
            if(isLValueOnly(i)) {
                argIsConst[i] = false
            } else {
                argIsConst[i] = (index % 2) == 1
                index /= 2
            }
        }

        require(kindIndex == 0)

        return argIsConst
    }

    fun getArgKindIndex(signatureIndex: Int, args: List<Node>): Int {
        val signature = signatures[signatureIndex]

        var index = 0

        for(i in signature.argTypes.indices.reversed()) {
            if(!isLValueOnly(i)) {
                index *= 2

                if(args[i] is ConstValue) {
                    index += 1
                }
            } else {
                require(args[i] is IdNode)
            }
        }

        return index
    }

}
