/* This file is generated */
package at.searles.meelan

import at.searles.parsing.Mapping
import at.searles.parsingtools.*
import at.searles.parsingtools.list.CreateEmptyList
import at.searles.parsingtools.list.CreateSingletonList
import at.searles.parsingtools.properties.CreateEmptyProperties
import at.searles.parsingtools.properties.CreateObject
import at.searles.parsingtools.properties.PutProperty

import at.searles.meelan.ops.*
import at.searles.meelan.nodes.*


import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Ref
import at.searles.parsingtools.generator.Context
import at.searles.parsingtools.opt
import at.searles.parsingtools.rep
import at.searles.regex.CharSet
import at.searles.regex.Regex

object Meelan {

    private val tokenizer = SkipTokenizer(Lexer())
    private val context = Context(tokenizer)
    

    val app = Ref<Node>("app")
    val ifExpr = Ref<Node>("ifExpr")
    val literal = Ref<Node>("literal")
    val pow = Ref<Node>("pow")
    val expr = Ref<Node>("expr")
    val stmt = Ref<Node>("stmt")
    val stmts = Ref<List<Node>>("stmts")
    // position 497-556
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 566-623
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 679-705
    val slComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 718-750
    val mlComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).or(CharSet.interval(0, 12, 14, 2147483646)).rep()))

    // position 754-955
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 970-988
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 1004-1032
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 1048-1067
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 1083-1107
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1123-1179
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1195-1232
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1248-1287
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1295-1325
    val intNum = context.parser(intRex, toInt)

    // position 1332-1365
    val realNum = context.parser(realRex, toReal)

    // position 1372-1402
    val hexNum = context.parser(hexRex, toHex)

    // position 1409-1445
    val str = context.parser(stringRex, toEscString)

    // position 1452-1491
    val bool = context.parser(Regex.text("true").or(Regex.text("false")), toBool)

    // position 1498-1544
    val identifier = context.parser(identifierRex, toIdString)

    // position 1552-1590
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1597-1627
    val realNode = realNum.then(toRealNode)

    // position 1634-1664
    val stringNode = str.then(toStringNode)

    // position 1671-1700
    val idNode = identifier.then(toIdNode)

    // position 1707-1734
    val boolNode = bool.then(toBoolNode)

    // position 1742-1768
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1775-1803
    val exprList = expr.list(comma)

    // position 1811-1854
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1862-2080
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(idNode.annotate(Annot.Id)).or(boolNode.annotate(Annot.Num)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 2088-2150
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2157-2200
    val singleArgument = app.then(CreateSingletonList())

    // position 2207-2267
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2323-2396
    init {
        app.set(atom.then(arguments.fold(toApp).or(context.text(".").then(identifier.fold(toQualified))).rep()))
    }

    // position 2443-2897
    init {
        ifExpr.set(context.text("if").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Kw).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2902-2932
    val block = context.text("{").then(stmts).then(toBlock).then(context.text("}"))

    // position 2937-2973
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 2981-3017
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 3025-3150
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3181-3229
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3237-3282
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3290-3392
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3400-3480
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3485-3716
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3721-3763
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3767-3828
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3832-3893
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 3897-3955
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 3960-3981
    init {
        expr.set(logicalOr)
    }

    // position 3986-4034
    val exprstmt = expr.then(context.text("=").then(expr.fold(toBinary(Assign))).opt())

    // position 4039-4276
    val whilestmt = context.text("while").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4281-4516
    val forstmt = context.text("for").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4521-4563
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4568-4853
    val vardecl = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4858-5070
    val valdecl = context.text("val").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5075-5345
    val parameter = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5350-5385
    val parameters = parameter.list(comma)

    // position 5390-5667
    val fundecl = context.text("fun").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5672-5964
    val classdecl = context.text("class").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 5969-6014
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl)

    // position 6022-6063
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6071-6132
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true))

    // position 6136-6176
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6180-6204
    val program = stmts.then(toBlock)

}
