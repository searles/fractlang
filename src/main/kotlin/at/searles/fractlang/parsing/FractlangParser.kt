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


    val appArgument = Ref<List<Node>>("appArgument")
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
    val intNum = context.parser(intRex, toInt).ref("intNum")

    // position 1225-1258
    val realNum = context.parser(realRex, toReal).ref("realNum")

    // position 1265-1295
    val hexNum = context.parser(hexRex, toHex).ref("hexNum")

    // position 1302-1338
    val str = context.parser(stringRex, toEscString).ref("str")

    // position 1345-1361
    val boolTrue = context.text("true").ref("boolTrue")

    // position 1368-1386
    val boolFalse = context.text("false").ref("boolFalse")

    // position 1393-1450
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false))).ref("bool")

    // position 1457-1503
    val identifier = context.parser(identifierRex, toIdString).ref("identifier")

    // position 1511-1549
    val intNode = intNum.or(hexNum).then(toIntNode).ref("intNode")

    // position 1556-1586
    val realNode = realNum.then(toRealNode).ref("realNode")

    // position 1593-1623
    val stringNode = str.then(toStringNode).ref("stringNode")

    // position 1630-1659
    val idNode = identifier.then(toIdNode).ref("idNode")

    // position 1666-1693
    val boolNode = bool.then(toBoolNode).ref("boolNode")

    // position 1701-1875
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id)).ref("atom")

    // position 1888-1965
    val qualifier = context.text(".").then(identifier.fold(toQualified)).or(context.text("[").then(expr.fold(toIndexed)).then(context.text("]"))).ref("qualifier")

    // position 1977-2003
    val comma = context.text(",").annotate(Annot.Comma).ref("comma")

    // position 2010-2038
    val exprList = expr.list(comma).ref("exprList")

    // position 2043-2185
    val appHead = atom.or(context.text("[").then(exprList.annotate(Annot.Intent)).then(toVectorNode).then(context.text("]"))).or(context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))).or(context.text("(").then(expr).then(context.text(")"))).then(qualifier.rep()).ref("appHead")

    // position 2193-2253
    val argumentList = context.text("(").then(exprList).then(context.text(")")).then(appArgument.fold(listApply).opt()).ref("argumentList")

    // position 2290-2360
    val singleArgument = atom.then(appArgument.fold(toApp).opt()).then(CreateSingletonList()).ref("singleArgument")

    // position 2368-2434
    init {
        appArgument.set(singleArgument.or(argumentList, true))
    }

    // position 2471-2520
    val app = appHead.then(appArgument.fold(toApp).opt()).then(qualifier.rep()).ref("app")

    // position 2528-2992
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2997-3080
    val block = context.text("{").annotate(Annot.Newline).then(stmts.annotate(Annot.Intent)).then(toBlock.annotate(Annot.Newline)).then(context.text("}")).ref("block")

    // position 3088-3114
    val term = ifExpr.or(block).or(app).ref("term")

    // position 3162-3287
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3318-3366
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt()).ref("cons")

    // position 3374-3419
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3427-3529
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep()).ref("product")

    // position 3537-3617
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep()).ref("sum")

    // position 3622-3853
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt()).ref("cmp")

    // position 3858-3900
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp).ref("logicalLit")

    // position 3904-3965
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep()).ref("logicalAnd")

    // position 3969-4030
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep()).ref("logicalXor")

    // position 4034-4092
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep()).ref("logicalOr")

    // position 4097-4118
    init {
        expr.set(logicalOr)
    }

    // position 4123-4167
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt()).ref("exprstmt")

    // position 4172-4414
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body")).ref("whilestmt")

    // position 4419-4659
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body")).ref("forstmt")

    // position 4664-4706
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4711-5000
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init")).ref("vardecl")

    // position 5005-5221
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init")).ref("valdecl")

    // position 5226-5500
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode).ref("parameter")

    // position 5505-5540
    val parameters = parameter.list(comma).ref("parameters")

    // position 5545-5826
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body")).ref("fundecl")

    // position 5831-6127
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body")).ref("classdecl")

    // position 6132-6404
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(expr.fold(PutProperty("description"))).opt()).then(context.text("=")).then(str.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr")).ref("externdecl")

    // position 6409-6467
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl).ref("decl")

    // position 6475-6516
    val semicolon = context.text(";").then(Mapping.identity<Node>()).ref("semicolon")

    // position 6524-6600
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt)).ref("stmtOrDecl")

    // position 6603-6643
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6647-6671
    val program = stmts.then(toBlock).ref("program")

}
