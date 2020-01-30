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


    // position 866-880
    val intRex: Regex = CharSet.interval(48, 57).plus()

    // position 896-924
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 940-959
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 975-999
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1015-1072
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1088-1125
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1141-1180
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1188-1218
    val intNum = context.parser(intRex, toInt)

    // position 1225-1258
    val realNum = context.parser(realRex, toReal)

    // position 1265-1295
    val hexNum = context.parser(hexRex, toHex)

    // position 1302-1338
    val str = context.parser(stringRex, toEscString)

    // position 1345-1361
    val boolTrue = context.text("true")

    // position 1368-1386
    val boolFalse = context.text("false")

    // position 1393-1450
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false)))

    // position 1457-1503
    val identifier = context.parser(identifierRex, toIdString)

    // position 1511-1549
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1556-1586
    val realNode = realNum.then(toRealNode)

    // position 1593-1623
    val stringNode = str.then(toStringNode)

    // position 1630-1659
    val idNode = identifier.then(toIdNode)

    // position 1666-1693
    val boolNode = bool.then(toBoolNode)

    // position 1701-1875
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id))

    // position 1888-1965
    val qualifier = context.text(".").then(identifier.fold(toQualified)).or(context.text("[").then(expr.fold(toIndexed)).then(context.text("]")))

    // position 1977-2003
    val comma = context.text(",").annotate(Annot.Comma)

    // position 2010-2038
    val exprList = expr.list(comma)

    // position 2043-2185
    val appHead = atom.or(context.text("[").then(exprList.annotate(Annot.Intent)).then(toVectorNode).then(context.text("]"))).or(context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))).or(context.text("(").then(expr).then(context.text(")"))).then(qualifier.rep())

    // position 2190-2234
    val singleArgument = atom.then(CreateSingletonList())

    // position 2241-2271
    val argumentList = context.text("(").then(exprList).then(context.text(")"))

    // position 2279-2331
    val appArgument = singleArgument.or(argumentList, true)

    // position 2368-2451
    val app = appHead.then(appArgument.then(appArgument.fold(listApply).rep()).fold(toApp).opt()).then(qualifier.rep())

    // position 2459-2923
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2928-3011
    val block = context.text("{").annotate(Annot.Newline).then(stmts.annotate(Annot.Intent)).then(toBlock.annotate(Annot.Newline)).then(context.text("}"))

    // position 3019-3045
    val term = ifExpr.or(block).or(app)

    // position 3093-3218
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3249-3297
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3305-3350
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3358-3460
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3468-3548
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3553-3784
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3789-3831
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3835-3896
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3900-3961
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 3965-4023
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 4028-4049
    init {
        expr.set(logicalOr)
    }

    // position 4054-4098
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt())

    // position 4103-4345
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4350-4590
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4595-4637
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4642-4931
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4936-5152
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5157-5431
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5436-5471
    val parameters = parameter.list(comma)

    // position 5476-5757
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5762-6058
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 6063-6335
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(expr.fold(PutProperty("description"))).opt()).then(context.text("=")).then(str.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr"))

    // position 6340-6398
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl)

    // position 6406-6447
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6455-6531
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt))

    // position 6534-6574
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6578-6602
    val program = stmts.then(toBlock)

}
