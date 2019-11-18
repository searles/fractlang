package at.searles.meelan.nodes

import at.searles.meelan.Type
import at.searles.meelan.Visitor
import at.searles.parsing.Trace

abstract class Node(val trace: Trace) {

    /**
     * Only initialized during semantic analysis
     */
    lateinit var type: Type

    abstract fun <T> accept(visitor: Visitor<T>): T
}