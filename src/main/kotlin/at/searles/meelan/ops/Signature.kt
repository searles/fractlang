package at.searles.meelan.ops

import at.searles.meelan.nodes.Node
import at.searles.meelan.Type

class Signature(val returnType: Type, vararg val argTypes: Type) {
    fun convertArguments(vararg args: Node): Array<Node>? {
        if(args.size < argTypes.size) {
            return null
        }

        return argTypes.zip(args)
            .map {it.first.convert(it.second)}
            .toTypedArray()
    }
}