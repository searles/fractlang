package at.searles.fractlang.parsing

import at.searles.fractlang.nodes.*
import at.searles.fractlang.ops.*
import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.lexer.Tokenizer
import at.searles.parsing.*
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.tokens.TokenParser
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.parsingtools.common.PairCreator
import at.searles.parsingtools.common.ValueInitializer
import at.searles.parsingtools.list.ListCreator
import at.searles.regexp.CharSet
import at.searles.regexp.Text
import at.searles.regexparser.RegexpParser

open class Grammar<T: Tokenizer>(val tokenizer: T) {

    val eof by lazy {
        val regexp = CharSet.eof()
        val tokenId = tokenizer.add(regexp)
        TokenRecognizer(tokenId, tokenizer, "")
    }

    fun regexOf(regexStr: String): TokenParser {
        val regex = RegexpParser.parse(regexStr)
        val tokenId = tokenizer.add(regex)

        return TokenParser(tokenId, tokenizer)
    }

    fun <T> regexOf(regexStr: String, conversion: (CharSequence) -> T): Parser<T> {
        val mapping = Mapping.create<CharSequence, T>( { v -> v.toString() } ) { s -> conversion(s) }
        return regexOf(regexStr) + mapping
    }

    fun keywordOf(text: String): TokenRecognizer {
        val regex = Text(text)
        val tokenId = tokenizer.add(regex)
        return TokenRecognizer(tokenId, tokenizer, text)
    }

    operator fun <T, U> Reducer<T, U>.plus(right: String): Reducer<T, U> {
        return this + keywordOf(right)
    }

    operator fun <T> Parser<T>.plus(right: String): Parser<T> {
        return this + keywordOf(right)
    }

    operator fun Recognizer.plus(right: String): Recognizer {
        return this + keywordOf(right)
    }

    fun <C> String.annotate(annotation: C): Recognizer {
        return keywordOf(this).annotate(annotation)
    }

    fun String.ref(label: String): Recognizer {
        return keywordOf(this).ref(label)
    }
}

object FractlangGrammar: Grammar<SkipTokenizer>(SkipTokenizer(Lexer())) {

    val whiteSpace = regexOf("""[\t\x0b\r\n\x0c \x85\xa0\u1680\u2000-\u200a\u2028\u2029\u202f\u205f\u3000]+""")
    val multilineComment = regexOf("""('/*' .* '*/')!""")
    val singlelineComment = regexOf("""'//' [^\n]*""")

    init {
        tokenizer.addSkipped(whiteSpace.tokenId)
        tokenizer.addSkipped(singlelineComment.tokenId)
        tokenizer.addSkipped(multilineComment.tokenId)
    }

    val intRex = "[0-9]+"
    val hexRex = "'#' [0-9A-Fa-f]{1,8}"
    val decimals = "'.'[0-9]*"
    val exponent = "[eE]'-'?[0-9]+"
    val realRex = "[0-9]+ ($decimals | $exponent | $decimals $exponent)"
    val identifierRex = "[a-zA-Z_][a-zA-Z0-9_]* - ('else' | 'if' | 'while' | 'val' | 'var' | 'fun' | 'for' | 'in' | 'class' | 'extern' | 'and' | 'or' | 'xor' | 'true' | 'false')"
    val stringRex = """('"' ([^\"] | '\\'. )* '"')"""

    val intNum = regexOf(intRex) { toInt(it) }
    val realNum = regexOf(realRex) { toReal(it) }
    val hexNum = regexOf(hexRex) { toHex(it) }
    val identifier = regexOf(identifierRex) { toIdString(it) }

    val str = regexOf(stringRex) + EscStringMapper

    val bool =
            keywordOf("true") + ValueInitializer(true) or
            keywordOf("false") + ValueInitializer(false)

    val intNode = (intNum or hexNum) + IntNode.Creator
    val realNode = realNum + RealNode.Creator
    val stringNode = str + StringNode.Creator
    val idNode = identifier + IdNode.Creator
    val boolNode = bool + BoolNode.Creator

    val atom = intNode.annotate(Annot.Const) or
            realNode.annotate(Annot.Const) or
            stringNode.annotate(Annot.Str) or
            boolNode.annotate(Annot.Const) or
            idNode.annotate(Annot.Name)

    val comma = ",".annotate(Annot.Comma)

    val expr = Ref<Node>("expr")
    val stmt = Ref<Node>("stmt")
    val stmts = Ref<List<Node>>("stmts")
    val app = Ref<Node>("app")

    val exprList = expr.rep(comma)

    val qualifier = keywordOf(".") + identifier + (QualifiedNode.Creator)
    val index = keywordOf("[") + expr + (IndexedNode.Creator) + "]"
    val multiArgument = keywordOf("(") + exprList + AppChain.Creator + ")"
    val singleArgument = (app + ListCreator()) + AppChain.Creator

    val vector = keywordOf("[") + exprList + VectorNode.Creator + "]"

    val appHead = atom or
            vector or
            keywordOf("(") + expr + ")"

    // abs is tough: |1 + log |x|| and |1 + x| y
    // using a flag is no solution: |1 + (log |x|)|
    init {
        app.ref = appHead + (qualifier or index or multiArgument or singleArgument).rep()
    }

    val ifExpr = "if".annotate(Annot.Keyword) +
            "(" + expr + ")" +
            (stmt + If.Creator) + (
                "else".annotate(Annot.Keyword) + stmt + IfElse.Creator
            ).opt()

    val block = "{".annotate(Annot.Newline) + stmts.annotate(Annot.Intent).annotate(Annot.Newline) + Block.Creator + "}"

    val term = ifExpr or block or app

    val literal = Ref<Node>("literal")

    val literalRef = 
            keywordOf("-") + literal + UnaryCreator(Neg) or
            keywordOf("/") + literal + UnaryCreator(Recip) or
            term

    init { literal.ref = literalRef }

    val cons = literalRef + (keywordOf(":") + literalRef + (BinaryCreator(Cons))).opt()

    val powRef = Ref<Node>("pow")

    val pow = cons + (keywordOf("^") + powRef + (BinaryCreator(Pow)) ).opt()
    
    init { powRef.ref = pow }

    val product = pow + (
            keywordOf("*") + pow + (BinaryCreator(Mul)) or 
            keywordOf("/") + pow + (BinaryCreator(Div)) or 
            keywordOf("%") + pow + (BinaryCreator(Mod))
    ).rep()

    val sum = product + (
            keywordOf("+") + product + (BinaryCreator(Add)) or 
            keywordOf("-") + product + (BinaryCreator(Sub))
    ).rep()

    val cmp = sum + ( 
            keywordOf(">") +  sum + (BinaryCreator(Greater)) or 
            keywordOf(">=") + sum + (BinaryCreator(GreaterEqual)) or
            keywordOf("<=") + sum + (BinaryCreator(LessEqual)) or
            keywordOf("<") + sum + (BinaryCreator(Less)) or
            keywordOf("==") + sum + (BinaryCreator(Equal)) or
            keywordOf("!=") + sum + (BinaryCreator(NotEqual))
    ).opt()

    val logicalLit = keywordOf("not") + cmp + UnaryCreator(Not) or cmp
    val logicalAnd = logicalLit + (keywordOf("and") + logicalLit + (BinaryCreator(And))).rep()
    val logicalXor = logicalAnd + (keywordOf("xor") + logicalAnd + (BinaryCreator(Xor))).rep()
    val logicalOr = logicalXor + (keywordOf("or") + logicalXor + (BinaryCreator(Or))).rep()

    init { expr.ref = logicalOr }

    val exprstmt = expr + (keywordOf("=") + expr + (Assignment.Creator)).opt()

    val whilestmt = "while".annotate(Annot.Keyword) +
        "(" + expr + ")" + (
            (stmt orSwapOnPrint Nop.Creator) + While.Creator
        )


    // TODO eliminate mapcreator
    val forstmt = "for".annotate(Annot.Keyword) + "(" +
            identifier + "in" + (expr + PairCreator<String, Node>()) + ")" +
            (stmt + For.Creator)

    init { stmt.ref = whilestmt or forstmt or exprstmt }

    val valdecl = "val".annotate(Annot.DefKeyword) + identifier + "=" + (expr + ValDecl.Creator)


    val varParameter = "var".annotate(Annot.DefKeyword) + identifier + (
            keywordOf(":") + identifier + (VarParameter.CreatorWithType) or
            VarParameter.CreatorWithoutType
    )

    val parameters = (varParameter or idNode).rep(comma)

    val signature: Parser<Pair<String, List<Node>>> = identifier + "(" + (parameters + PairCreator<String, List<Node>>()) + ")"

    val fundecl = "fun".annotate(Annot.DefKeyword) + signature +
            ((block or keywordOf("=") + expr) + FunDecl.Creator)

    val classdecl = "class".annotate(Annot.DefKeyword) +
            signature +
            (block + ClassDecl.Creator)

    val externdecl = "extern".annotate(Annot.DefKeyword) + identifier +
            (keywordOf(":") + expr + (PairCreator<String, Node>())) +
            "=" + (str + ExternDecl.Creator)

    val vardecl = varParameter + (
            keywordOf("=") + expr + (VarDecl.CreatorWithInit) or
            VarDecl.CreatorWithoutInit
    )


    val decl = vardecl or valdecl or fundecl or classdecl or externdecl

    val semicolon = keywordOf(";") + Mapping.identity<Node>()

    val stmtOrDecl = ((decl or stmt) + (semicolon orSwapOnPrint SkipSemicolon)).annotate(Annot.Stmt)

    init { stmts.ref = stmtOrDecl.rep() }

    val program = stmts + Block.Creator
}