package at.searles.meelan

import at.searles.meelan.nodes.CplxNode
import at.searles.meelan.nodes.IntNode
import at.searles.meelan.nodes.Node
import at.searles.meelan.nodes.RealNode
import at.searles.meelan.ops.Cons
import at.searles.meelan.ops.ToReal
import at.searles.parsing.Trace

enum class BaseTypes: Type {
    Int {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            return IntNode(trace, value)
        }

        override fun commonType(type: Type): Type? {
            return when(type) {
                Int -> Int
                else -> null
            }
        }

        override fun canConvert(node: Node): Boolean {
            return when(node.type) {
                Int -> true
                else -> false
            }
        }

        override fun convert(node: Node): Node {
            return when(node.type) {
                Int -> node
                else -> throw SemanticAnalysisException("cannot convert to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 4
    }, Real {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            return RealNode(trace, value.toDouble())
        }

        override fun commonType(type: Type): Type? {
            return when(type) {
                Int -> Real
                Real -> Real
                else -> null
            }
        }

        override fun canConvert(node: Node): Boolean {
            return when(node.type) {
                Int -> true
                Real -> true
                else -> false
            }
        }

        override fun convert(node: Node): Node {
            return when(node.type) {
                Int -> ToReal.apply(node.trace, listOf(node))
                Real -> node
                else -> throw SemanticAnalysisException("cannot convert to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 8
    }, Cplx {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            return CplxNode(trace, at.searles.commons.math.Cplx(value.toDouble()))
        }

        override fun commonType(type: Type): Type? {
            return when(type) {
                Int -> Cplx
                Real -> Cplx
                Cplx -> Cplx
                else -> null
            }
        }

        override fun canConvert(node: Node): Boolean {
            return when(node.type) {
                Int -> true
                Real -> true
                Cplx -> true
                else -> false
            }
        }

        override fun convert(node: Node): Node {
            return when(node.type) {
                Int -> Cons.apply(node.trace,
                    listOf(
                        ToReal.apply(node.trace, listOf(node)),
                        RealNode(node.trace, 0.0)
                    ))
                Real -> Cons.apply(node.trace,
                    listOf(
                        node,
                        RealNode(node.trace, 0.0)
                    ))
                Cplx -> node
                else -> throw SemanticAnalysisException("cannot convert to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 16
    }, Bool {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            throw IllegalArgumentException()
        }

        override fun commonType(type: Type): Type? {
            return if(type == this) this else null
        }

        override fun canConvert(node: Node): Boolean {
            return node.type == this
        }

        override fun convert(node: Node): Node {
            if(node.type == this) {
                return node
            } else {
                throw SemanticAnalysisException("cannot convert ${node.type} to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 0
    }, Unit {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            throw IllegalArgumentException()
        }

        override fun commonType(type: Type): Type? {
            return if(type == this) this else null
        }

        override fun canConvert(node: Node): Boolean {
            return node.type == this
        }

        override fun convert(node: Node): Node {
            if(node.type == this) {
                return node
            } else {
                throw SemanticAnalysisException("cannot convert ${node.type} to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 0
    }, String {
        override fun createNumNode(trace: Trace, value: kotlin.Int): Node {
            throw IllegalArgumentException()
        }

        override fun commonType(type: Type): Type? {
            return if(type == this) this else null
        }

        override fun canConvert(node: Node): Boolean {
            return node.type == this
        }

        override fun convert(node: Node): Node {
            if(node.type == this) {
                return node
            } else {
                throw SemanticAnalysisException("cannot convert ${node.type} to ${this.name}", node.trace)
            }
        }

        override fun byteSize(): kotlin.Int = 0
    };

    abstract fun createNumNode(trace: Trace, value: kotlin.Int): Node
}