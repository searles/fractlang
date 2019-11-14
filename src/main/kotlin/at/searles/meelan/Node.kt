package at.searles.meelan

import at.searles.parsing.Trace

abstract class Node(val trace: Trace) {

    /**
     * Only initialized during semantic analysis
     */
    lateinit var type: Type

    abstract fun <T> accept(visitor: Visitor<T>): T
}