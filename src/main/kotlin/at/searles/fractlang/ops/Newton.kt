package at.searles.fractlang.ops

import at.searles.fractlang.nodes.Node
import at.searles.parsing.Trace

object Newton: Op {
    override fun apply(trace: Trace, args: List<Node>): Node {
        return Sub.apply(trace,
            args[1],
            Div.apply(trace,
                args[0],
                Diff.apply(trace, args)
            )
        )
    }
}