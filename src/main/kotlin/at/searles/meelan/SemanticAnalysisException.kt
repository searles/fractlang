package at.searles.meelan

import at.searles.parsing.Trace
import java.lang.RuntimeException

class SemanticAnalysisException(msg: String, trace: Trace) : RuntimeException(
    msg + "@${trace.start}-${trace.end}"
)