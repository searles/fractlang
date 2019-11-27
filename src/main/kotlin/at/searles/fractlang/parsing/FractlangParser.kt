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
    // position 523-582
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 592-649
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 705-731
    val slComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 744-776
    val mlComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).or(CharSet.interval(0, 12, 14, 2147483646)).rep()))

    // position 780-981
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 996-1014
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 1030-1058
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 1074-1093
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 1109-1133
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1149-1205
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1221-1258
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1274-1313
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1321-1351
    val intNum = context.parser(intRex, toInt)

    // position 1358-1391
    val realNum = context.parser(realRex, toReal)

    // position 1398-1428
    val hexNum = context.parser(hexRex, toHex)

    // position 1435-1471
    val str = context.parser(stringRex, toEscString)

    // position 1478-1494
    val boolTrue = context.text("true")

    // position 1501-1519
    val boolFalse = context.text("false")

    // position 1526-1583
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false)))

    // position 1590-1636
    val identifier = context.parser(identifierRex, toIdString)

    // position 1644-1682
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1689-1719
    val realNode = realNum.then(toRealNode)

    // position 1726-1756
    val stringNode = str.then(toStringNode)

    // position 1763-1792
    val idNode = identifier.then(toIdNode)

    // position 1799-1826
    val boolNode = bool.then(toBoolNode)

    // position 1834-1860
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1867-1895
    val exprList = expr.list(comma)

    // position 1903-1946
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1954-2172
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 2180-2242
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2249-2292
    val singleArgument = app.then(CreateSingletonList())

    // position 2299-2359
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2415-2488
    init {
        app.set(atom.then(arguments.fold(toApp).or(context.text(".").then(identifier.fold(toQualified))).rep()))
    }

    // position 2535-2999
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 3004-3050
    val block = context.text("{").then(stmts).then(toBlock.annotate(Annot.Block)).then(context.text("}"))

    // position 3055-3091
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 3099-3135
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 3143-3268
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3299-3347
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3355-3400
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3408-3510
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3518-3598
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3603-3834
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3839-3881
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3885-3946
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3950-4011
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 4015-4073
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 4078-4099
    init {
        expr.set(logicalOr)
    }

    // position 4104-4148
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt())

    // position 4153-4395
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4400-4640
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4645-4687
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4692-4981
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4986-5202
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5207-5481
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5486-5521
    val parameters = parameter.list(comma)

    // position 5526-5807
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5812-6108
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 6113-6395
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":")).then(stringNode.fold(PutProperty("description"))).then(context.text("=")).then(stringNode.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr"))

    // position 6400-6458
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl)

    // position 6466-6507
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6515-6591
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt))

    // position 6594-6634
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6638-6662
    val program = stmts.then(toBlock)

}
