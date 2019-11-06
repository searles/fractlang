package at.searles.meelan

abstract class BaseOp(vararg val signatures: Signature) {
// FIXME    override fun apply(vararg arguments: Node): Node? {
//        // arguments may be longer than signature. Individual instructions can decide how to handle this.
//        // The last element in signatures is the return type. If it is 'unit', it means that the function does
//        // not return anything.
//        val args = signatures.map{it.convertArguments(*arguments)}.filterNotNull().firstOrNull() ?: return null
//        // todo
//        return null
//    }
//
}