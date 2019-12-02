package at.searles.fractlang.parsing

import at.searles.parsingtools.formatter.CodeFormatter

object FractlangFormatter: CodeFormatter(FractlangParser.ws.tokenId, FractlangParser.program) {
    init {
        addIndentAnnotation(Annot.Intent)
        addForceNewlineAnnotation(Annot.Newline)
    }
}