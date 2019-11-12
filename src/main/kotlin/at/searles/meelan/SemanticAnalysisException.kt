package at.searles.meelan

import java.lang.RuntimeException

class SemanticAnalysisException(msg: String, node: Node) : RuntimeException(
    msg + "@${node.trace.start}-${node.trace.end}"
)