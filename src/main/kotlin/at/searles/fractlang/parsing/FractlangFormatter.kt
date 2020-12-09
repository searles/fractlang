package at.searles.fractlang.parsing

import at.searles.parsing.format.FormatRules
import at.searles.parsingtools.formatter.CodeFormatter

object FractlangFormatter: CodeFormatter(FormatRules(), FractlangGrammar.program, FractlangGrammar.whiteSpaceId) {
    init {
        // TODO!!!
//        addIndentLabel(Annot.Intent)
//        addForceNewlineLabel(Annot.Newline)
    }
}