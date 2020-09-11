package at.searles.fractlang.parsing

import at.searles.parsingtools.formatter.CodeFormatter

object FractlangFormatter: CodeFormatter(FractlangGrammar.whiteSpace.tokenId, FractlangGrammar.program) {
    init {
        addIndentLabel(Annot.Intent)
        addForceNewlineLabel(Annot.Newline)
    }
}