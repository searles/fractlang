package at.searles.fractlang.nodes

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.Visitor
import at.searles.fractlang.ops.BaseOp
import at.searles.fractlang.ops.Op
import at.searles.parsing.Trace

class OpNode(trace: Trace, val op: Op) : Node(trace) {

    init {
        if(op is BaseOp && op.signatures.size == 1 && op.signatures[0].argTypes.isEmpty()) {
            type = op.signatures[0].returnType
        } else {
            type = BaseTypes.Unit
        }
    }

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

}
