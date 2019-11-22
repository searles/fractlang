package at.searles.meelan

object Optimizer {

	private fun allNum(nodes: List<Node>) {
		return nodes.all(it is NumNode)
	}

	private fun isOp(node: Node, op: Op): Boolean {
		return node is App && node.head is OpNode && node.head.op == op
	}

	private fun isZero(arg: Node): Boolean {
		return arg is NumNode && arg.isZero()
	}

	private fun isOne(arg: Node): Boolean {
		return arg is NumNode && arg.isOne()
	}

	fun add(trace: Trace, args: ListNode<Node>): Node {
		// N + N -> N
		if(allNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value + (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value + (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().add(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x + N -> N + x
		if(args[1] is ConstValue) return add(trace, listOf(args[1], args[0]))
		
		// 0 + x -> x
		if(isZero(args[0])) return args[1]

		// x + (y + z) -> (x + y) + z
		if(isOp(args[1], Add)) return Add.apply(trace, listOf(
				add.apply(trace, args[0], (args[1] as App).args[0]), 
				(args[1] as App).args[1]))

		// x + (y - z) -> (x + y) - z
		if(isOp(args[1], Sub)) return sub(trace, listOf(
				add(trace, args[0], (args[1] as App).args[0]), 
				(args[1] as App).args[1]))

		// x + -y -> x - y
		if(isOp(args[1], Neg)) return sub(trace, listOf(args[0], (args[1] as App).args[0]))

		// -x + y -> y - x
		if(isOp(args[0], Neg)) return sub(trace, listOf(args[1], (args[0] as App).args[0]))		
		
		return App(trace, Add, args)
	}

	fun sub(trace: Trace, args: List<Node>): Node {
		// N - N -> N
		if(allNum(args)) {
			return when(val arg0 = args[0]) {
				is IntNode -> IntNode(trace, arg0.value - (args[1] as IntNode).value)
				is RealNode -> RealNode(trace, arg0.value - (args[1] as RealNode).value)
				is CplxNode -> CplxNode(trace, Cplx().sub(arg0.value, (args[1] as CplxNode).value))
				else -> error("something went wrong with the cast")
			}
		}

		// x - N -> -N + x
		if(args[1] is ConstValue) return add(trace, listOf(neg(trace, listOf(args[1])), args[0]))
		
		// 0 - x -> -x
		if(isZero(args[0])) return neg(trace, args[1])

		// x - (y + z) -> (x - y) - z
		if(isOp(args[1], Add)) return sub(trace, listOf(
				sub(trace, args[0], (args[1] as App).args[0]), 
				(args[1] as App).args[1]))

		// x - (y - z) -> (x - y) + z
		if(isOp(args[1], Sub)) return add(trace, listOf(
				sub(trace, args[0], (args[1] as App).args[0]), 
				(args[1] as App).args[1]))

		// -x - y -> -(x + y)
		if(isOp(args[0], Neg)) return neg(trace, add(trace, listOf((args[0] as App).args[0], args[1]))
		
		// x - -y -> x + y
		if(isOp(args[1], Neg)) return add(trace, listOf(args[0], (args[1] as App).args[0]))
		
		return App(trace, Sub, args)
	}

	fun neg(trace: Trace, args: List<Node>): Node {
		// -N -> N
		if(allNum(args)) {
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
		if(isOp(args[0], Sub)) return sub(trace, listOf((args[0] as App).args[1], (args[0] as App).args[0]))

		return App(trace, Neg, args)
	}
}
