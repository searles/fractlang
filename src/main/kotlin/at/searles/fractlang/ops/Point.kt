package at.searles.fractlang.ops

import at.searles.fractlang.BaseTypes
import at.searles.fractlang.nodes.App
import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Point: BaseOp(Signature(BaseTypes.Cplx)) {

    override fun evaluate(trace: Trace, args: List<Node>): Node {
        return App(trace, this.toNode(trace), args).apply {
            this.type = BaseTypes.Cplx
        }
    }

    override fun countArgKinds(): Int {
        return 1
    }

    override fun getArgKindAt(offset: Int): List<ArgKind> {
        return emptyList()
    }

    override fun getArgKindOffset(args: List<Node>): Int {
        return 0
    }

    override fun getSignatureAt(offset: Int): Signature {
        return signatures[0]
    }
}