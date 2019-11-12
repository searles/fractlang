package at.searles.meelan

import at.searles.parsing.Trace

abstract class Node(val trace: Trace) {

    var type: Type? = null // null means not assigned yet.

    abstract fun <T> accept(visitor: Visitor<T>): T
}