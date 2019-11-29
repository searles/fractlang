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

    // position 534-593
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 603-660
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 716-742
    val mlComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 755-777
    val slComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).rep()))

    // position 793-994
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 1009-1027
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 1043-1071
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 1087-1106
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 1122-1146
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1162-1218
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1234-1271
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1287-1326
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1334-1364
    val intNum = context.parser(intRex, toInt)

    // position 1371-1404
    val realNum = context.parser(realRex, toReal)

    // position 1411-1441
    val hexNum = context.parser(hexRex, toHex)

    // position 1448-1484
    val str = context.parser(stringRex, toEscString)

    // position 1491-1507
    val boolTrue = context.text("true")

    // position 1514-1532
    val boolFalse = context.text("false")

    // position 1539-1596
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false)))

    // position 1603-1649
    val identifier = context.parser(identifierRex, toIdString)

    // position 1657-1695
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1702-1732
    val realNode = realNum.then(toRealNode)

    // position 1739-1769
    val stringNode = str.then(toStringNode)

    // position 1776-1805
    val idNode = identifier.then(toIdNode)

    // position 1812-1839
    val boolNode = bool.then(toBoolNode)

    // position 1847-1873
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1880-1908
    val exprList = expr.list(comma)

    // position 1916-1959
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1967-2185
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 2193-2255
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2262-2305
    val singleArgument = app.then(CreateSingletonList())

    // position 2312-2372
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2428-2501
    init {
        app.set(atom.then(arguments.fold(toApp).or(context.text(".").then(identifier.fold(toQualified))).rep()))
    }

    // position 2548-3012
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 3017-3063
    val block = context.text("{").then(stmts).then(toBlock.annotate(Annot.Block)).then(context.text("}"))

    // position 3068-3104
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 3112-3148
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 3156-3281
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3312-3360
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3368-3413
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3421-3523
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3531-3611
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3616-3847
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3852-3894
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3898-3959
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3963-4024
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 4028-4086
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 4091-4112
    init {
        expr.set(logicalOr)
    }

    // position 4117-4161
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt())

    // position 4166-4408
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4413-4653
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4658-4700
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4705-4994
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4999-5215
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5220-5494
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5499-5534
    val parameters = parameter.list(comma)

    // position 5539-5820
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5825-6121
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 6126-6394
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":")).then(str.fold(PutProperty("description"))).then(context.text("=")).then(str.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr"))

    // position 6399-6457
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl)

    // position 6465-6506
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6514-6590
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt))

    // position 6593-6633
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6637-6661
    val program = stmts.then(toBlock)

}
