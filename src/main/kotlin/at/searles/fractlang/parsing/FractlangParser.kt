/* This file is generated */
package at.searles.fractlang.parsing

import at.searles.parsing.Mapping
import at.searles.parsingtools.*
import at.searles.parsingtools.list.CreateEmptyList
import at.searles.parsingtools.list.CreateSingletonList
import at.searles.parsingtools.properties.CreateEmptyProperties
import at.searles.parsingtools.properties.CreateObject
import at.searles.parsingtools.properties.PutProperty

import at.searles.fractlang.ops.*
import at.searles.fractlang.nodes.*


import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Ref
import at.searles.parsingtools.generator.Context
import at.searles.parsingtools.opt
import at.searles.parsingtools.rep
import at.searles.regex.CharSet
import at.searles.regex.Regex

object FractlangParser {

    private val tokenizer = SkipTokenizer(Lexer())
    private val context = Context(tokenizer)
    

    val app = Ref<Node>("app")
    val ifExpr = Ref<Node>("ifExpr")
    val literal = Ref<Node>("literal")
    val pow = Ref<Node>("pow")
    val expr = Ref<Node>("expr")
    val stmt = Ref<Node>("stmt")
    val stmts = Ref<List<Node>>("stmts")
    // position 502-520
    val eof = context.parser(Regex.eof())

    // position 534-613
    val ws = context.parser(CharSet.interval(9, 13, 32, 32, 133, 133, 160, 160, 5760, 5760, 8192, 8202, 8232, 8233, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 626-652
    val mlComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 665-687
    val slComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).rep()))

    // position 691-851
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 866-884
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 900-928
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 944-963
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 979-1003
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1019-1075
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1091-1128
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1144-1183
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1191-1221
    val intNum = context.parser(intRex, toInt)

    // position 1228-1261
    val realNum = context.parser(realRex, toReal)

    // position 1268-1298
    val hexNum = context.parser(hexRex, toHex)

    // position 1305-1341
    val str = context.parser(stringRex, toEscString)

    // position 1348-1364
    val boolTrue = context.text("true")

    // position 1371-1389
    val boolFalse = context.text("false")

    // position 1396-1453
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false)))

    // position 1460-1506
    val identifier = context.parser(identifierRex, toIdString)

    // position 1514-1552
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1559-1589
    val realNode = realNum.then(toRealNode)

    // position 1596-1626
    val stringNode = str.then(toStringNode)

    // position 1633-1662
    val idNode = identifier.then(toIdNode)

    // position 1669-1696
    val boolNode = bool.then(toBoolNode)

    // position 1704-1730
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1737-1765
    val exprList = expr.list(comma)

    // position 1773-1833
    val vectorNode = context.text("[").then(exprList.annotate(Annot.Intent)).then(toVectorNode).then(context.text("]"))

    // position 1841-2076
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr.annotate(Annot.Intent)).then(context.text(")")))

    // position 2084-2146
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2153-2196
    val singleArgument = app.then(CreateSingletonList())

    // position 2203-2263
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2319-2392
    init {
        app.set(atom.then(arguments.fold(toApp).or(context.text(".").then(identifier.fold(toQualified))).rep()))
    }

    // position 2439-2903
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2908-2991
    val block = context.text("{").annotate(Annot.Newline).then(stmts.annotate(Annot.Intent)).then(toBlock.annotate(Annot.Newline)).then(context.text("}"))

    // position 2996-3032
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 3040-3076
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 3084-3209
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3240-3288
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3296-3341
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3349-3451
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3459-3539
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3544-3775
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3780-3822
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3826-3887
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3891-3952
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 3956-4014
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 4019-4040
    init {
        expr.set(logicalOr)
    }

    // position 4045-4089
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt())

    // position 4094-4336
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4341-4581
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4586-4628
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4633-4922
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4927-5143
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5148-5422
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5427-5462
    val parameters = parameter.list(comma)

    // position 5467-5748
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5753-6049
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 6054-6322
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":")).then(str.fold(PutProperty("description"))).then(context.text("=")).then(str.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr"))

    // position 6327-6385
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl)

    // position 6393-6434
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6442-6518
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt))

    // position 6521-6561
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6565-6589
    val program = stmts.then(toBlock)

}
