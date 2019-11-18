package at.searles.meelan.ops

import at.searles.meelan.nodes.App
import at.searles.meelan.nodes.Node
import at.searles.meelan.SemanticAnalysisException
import at.searles.meelan.nodes.ConstValue
import at.searles.meelan.nodes.IdNode
import at.searles.parsing.Trace

abstract class BaseOp(private vararg val signatures: Signature) : Op {
    open fun findSignature(arguments: List<Node>): Signature? {
        // check L-value
        arguments.forEachIndexed { index, arg ->
            if(isLValueOnly(index) && arg !is IdNode) {
                return null
            }
        }

        return signatures.firstOrNull{
            signature -> signature.argTypes.size == arguments.size
                && signature.argTypes.zip(arguments).all { it.first.canConvert(it.second) }
        }
    }

    override fun apply(trace: Trace, args: List<Node>): Node {
        // find correct type
        val signature = findSignature(args)
        if(signature != null) {
            if(args.all { it is ConstValue }) {
                return eval(trace, args)
            }

            return App(trace, this, args).apply {
                this.type = signature.returnType
            }
        } else {
            throw SemanticAnalysisException("Invalid arguments for ${this}", trace)
        }
    }

    open fun isLValueOnly(argIndex: Int): Boolean {
        return false
    }

    abstract fun eval(trace: Trace, args: List<Node>): Node

    override fun toString(): String {
        return javaClass.simpleName
    }
}