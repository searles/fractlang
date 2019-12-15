package at.searles.fractlang.semanticanalysis

import at.searles.commons.math.Cplx
import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.*
import at.searles.parsing.Trace

object Optimizer {

	private fun createApp(trace: Trace, op: BaseOp, args: List<Node>): App {
		return App(trace, OpNode(trace, op), args).apply {
			this.type = op.signatures.first { it.matches(args) }.returnType
		}
	}

	private fun isAllNum(nodes: List<Node>): Boolean {
		return nodes.all {it is NumValue }
	}

	private fun isOp(node: Node, op: BaseOp): Boolean {
		return node is App && node.head is OpNode && node.head.op == op
	}

	private fun isZero(arg: Node): Boolean {
		return arg is NumValue && arg.isZero()
	}

	private fun isOne(arg: Node): Boolean {
		return arg is NumValue && arg.isOne()
	}

	fun add(trace: Trace, args: List<Node>): Node {
		// N + N -> N
		if(isAllNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value + (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value + (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().add(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x + N -> N + x
		if(args[1] is ConstValue) return add(
            trace,
            listOf(args[1], args[0])
        )
		
		// 0 + x -> x
		if(isZero(args[0])) return args[1]

		// x + (y + z) -> (x + y) + z
		if(isOp(args[1], Add)) return Add.apply(trace, listOf(
            add(
                trace,
                listOf(args[0], (args[1] as App).args[0])
            ),
				(args[1] as App).args[1]))

		// x + (y - z) -> (x + y) - z
		if(isOp(
                args[1],
                Sub
            )
        ) return sub(
            trace, listOf(
                add(
                    trace,
                    listOf(args[0], (args[1] as App).args[0])
                ),
                (args[1] as App).args[1]
            )
        )

		// x + -y -> x - y
		if(isOp(
                args[1],
                Neg
            )
        ) return sub(
            trace,
            listOf(args[0], (args[1] as App).args[0])
        )

		// -x + y -> y - x
		if(isOp(
                args[0],
                Neg
            )
        ) return sub(
            trace,
            listOf(args[1], (args[0] as App).args[0])
        )
		
		return createApp(trace, Add, args)
	}

	fun sub(trace: Trace, args: List<Node>): Node {
		// N - N -> N
		if(isAllNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value - (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value - (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().sub(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x - N -> -N + x
		if(args[1] is ConstValue) return add(
            trace,
            listOf(neg(trace, listOf(args[1])), args[0])
        )
		
		// 0 - x -> -x
		if(isZero(args[0])) return neg(
            trace,
            listOf(args[1])
        )

		// x - (y + z) -> (x - y) - z
		if(isOp(
                args[1],
                Add
            )
        ) return sub(
            trace, listOf(
                sub(
                    trace,
                    listOf(args[0], (args[1] as App).args[0])
                ),
                (args[1] as App).args[1]
            )
        )

		// x - (y - z) -> (x - y) + z
		if(isOp(
                args[1],
                Sub
            )
        ) return add(
            trace, listOf(
                sub(
                    trace,
                    listOf(args[0], (args[1] as App).args[0])
                ),
                (args[1] as App).args[1]
            )
        )

		// -x - y -> -(x + y)
		if(isOp(
                args[0],
                Neg
            )
        ) return neg(
            trace,
            listOf(
                add(
                    trace,
                    listOf((args[0] as App).args[0], args[1])
                )
            )
        )
		
		// x - -y -> x + y
		if(isOp(
                args[1],
                Neg
            )
        ) return add(
            trace,
            listOf(args[0], (args[1] as App).args[0])
        )
		
		return createApp(trace, Sub, args)
	}

	fun neg(trace: Trace, args: List<Node>): Node {
		// -N -> N
		if(isAllNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, -arg0.value)
				is RealNode -> RealNode(trace, -arg0.value)
				is CplxNode -> CplxNode(trace, Cplx().neg(arg0.value))
				else -> error("something went wrong with the cast")
			}
		}

		// --x -> 
		if(isOp(args[0], Neg)) return (args[0] as App).args[0]

		// -(x - y) -> y - x
		if(isOp(
                args[0],
                Sub
            )
        ) return sub(
            trace,
            listOf((args[0] as App).args[1], (args[0] as App).args[0])
        )

		return createApp(trace, Neg, args)
	}

    fun abs(trace: Trace, args: List<Node>): Node {
		return when(val arg = args[0]) {
			is IntNode -> IntNode(trace, kotlin.math.abs(arg.value))
			is RealNode -> RealNode(trace, kotlin.math.abs(arg.value))
			is CplxNode -> CplxNode(trace, Cplx().abs(arg.value))
			else -> createApp(trace, Abs, args)
		}
    }

	fun mul(trace: Trace, args: List<Node>): Node {
		// N * N -> N
		if(isAllNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value * (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value * (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().mul(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x * N -> N * x
		if(args[1] is NumValue) return mul(
            trace,
            listOf(args[1], args[0])
        )

		// 0 * x -> 0
		if(isZero(args[0])) return args[0]

		// 1 * x -> 1
		if(isOne(args[0])) return args[1]

		// -x - y -> -(x * y)
		if(isOp(
                args[0],
                Neg
            )
        ) return neg(
            trace,
            listOf(
                mul(
                    trace,
                    listOf((args[0] as App).args[0], args[1])
                )
            )
        )

		// x * -y -> -(x * y)
		if(isOp(
                args[1],
                Neg
            )
        ) return neg(
            trace,
            listOf(
                mul(
                    trace,
                    listOf(args[0], (args[1] as App).args[0])
                )
            )
        )

		return createApp(trace, Mul, args)
	}

	fun div(trace: Trace, args: List<Node>): Node {
		// N - N -> N
		if(isAllNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value / (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value / (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().div(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x / N -> /N * x
		if(args[1] is NumValue) return mul(
            trace,
            listOf(
                recip(trace, listOf(args[1])),
                args[0]
            )
        )

		// 0 * x -> 0
		if(isZero(args[0])) return args[0]

		// 1 / x -> 1
		if(isOne(args[0])) return recip(
            trace,
            listOf(args[1])
        )

		// -x / y -> -(x / y)
		if(isOp(
                args[0],
                Neg
            )
        ) return neg(
            trace,
            listOf(
                div(
                    trace,
                    listOf((args[0] as App).args[0], args[1])
                )
            )
        )

		// x / -y -> -(x / y)
		if(isOp(
                args[1],
                Neg
            )
        ) return neg(
            trace,
            listOf(
                div(
                    trace,
                    listOf(args[0], (args[1] as App).args[0])
                )
            )
        )

		return createApp(trace, Div, args)
	}

	fun recip(trace: Trace, args: List<Node>): Node {
		// -N -> N
		if(isAllNum(args)) {
			return when(val arg = args[0]) {
				is RealNode -> RealNode(trace, 1.0 / arg.value)
				is CplxNode -> CplxNode(trace, Cplx().rec(arg.value))
				else -> error("something went wrong with the cast")
			}
		}

		// //x ->
		if(isOp(args[0], Reciprocal)) return (args[0] as App).args[0]

		// /(x / y) -> y / x
		if(isOp(
                args[0],
                Div
            )
        ) return div(
            trace,
            listOf((args[0] as App).args[1], (args[0] as App).args[0])
        )

		return createApp(trace, Reciprocal, args)
	}

    fun re(trace: Trace, args: List<Node>): Node {
        return when(args[0]) {
            is CplxNode -> RealNode(trace, (args[0] as CplxNode).value.re())
            else -> createApp(trace, RealPart, args)
        }
    }

    fun im(trace: Trace, args: List<Node>): Node {
        return when(args[0]) {
            is CplxNode -> RealNode(trace, (args[0] as CplxNode).value.im())
            else -> createApp(trace, RealPart, args)
        }
    }
}
