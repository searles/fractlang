/* This file is generated */
package at.searles.meelan;

import at.searles.parsingtools.*
import at.searles.parsingtools.list.CreateEmptyList
import at.searles.parsingtools.list.CreateSingletonList
import at.searles.parsingtools.properties.CreateEmptyProperties
import at.searles.parsingtools.properties.CreateObject
import at.searles.parsingtools.properties.PutProperty

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
    // position 377-436
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 446-503
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 559-585
    val slComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 598-630
    val mlComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).or(CharSet.interval(0, 12, 14, 2147483646)).rep()))

    // position 634-835
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 850-868
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 884-912
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 928-947
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 963-987
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1003-1059
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1075-1112
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1128-1167
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1175-1205
    val intNum = context.parser(intRex, toInt)

    // position 1212-1245
    val realNum = context.parser(realRex, toReal)

    // position 1252-1282
    val hexNum = context.parser(hexRex, toHex)

    // position 1289-1325
    val str = context.parser(stringRex, toEscString)

    // position 1332-1378
    val identifier = context.parser(identifierRex, toIdString)

    // position 1386-1424
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1431-1461
    val realNode = realNum.then(toRealNode)

    // position 1468-1498
    val stringNode = str.then(toStringNode)

    // position 1505-1534
    val idNode = identifier.then(toIdNode)

    // position 1542-1568
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1575-1603
    val exprList = expr.list(comma)

    // position 1611-1654
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1662-1844
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 1852-1902
    val qualified = atom.then(context.text(".").then(identifier.fold(toQualified)).rep())

    // position 1910-1989
    val arguments = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt()).or(app.then(CreateSingletonList()))

    // position 1997-2041
    init {
        app.set(qualified.then(arguments.fold(toApp).opt()))
    }

    // position 2088-2542
    init {
        ifExpr.set(context.text("if").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Kw).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2547-2577
    val block = context.text("{").then(stmts).then(toBlock).then(context.text("}"))

    // position 2582-2626
    val absExpr = context.text("|").then(expr).then(toUnary(BaseOps.Abs)).then(context.text("|"))

    // position 2634-2670
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 2678-2819
    init {
        literal.set(context.text("-").then(literal).then(toUnary(BaseOps.Neg)).or(context.text("/").then(literal).then(toUnary(BaseOps.Recip))).or(term))
    }

    // position 2850-2906
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(BaseOps.Cons))).opt())

    // position 2914-2967
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(BaseOps.Pow))).opt()))
    }

    // position 2975-3101
    val product = pow.then(context.text("*").then(pow.fold(toBinary(BaseOps.Mul))).or(context.text("/").then(pow.fold(toBinary(BaseOps.Div)))).or(context.text("%").then(pow.fold(toBinary(BaseOps.Mod)))).rep())

    // position 3109-3205
    val sum = product.then(context.text("+").then(product.fold(toBinary(BaseOps.Add))).or(context.text("-").then(product.fold(toBinary(BaseOps.Sub)))).rep())

    // position 3210-3489
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(BaseOps.Greater))).or(context.text(">=").then(sum.fold(toBinary(BaseOps.GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(BaseOps.LessEqual)))).or(context.text("<").then(sum.fold(toBinary(BaseOps.Less)))).or(context.text("==").then(sum.fold(toBinary(BaseOps.Equal)))).or(context.text("!=").then(sum.fold(toBinary(BaseOps.NotEqual)))).opt())

    // position 3494-3544
    val logicalLit = context.text("not").then(cmp).then(toUnary(BaseOps.Not)).or(cmp)

    // position 3548-3617
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(BaseOps.And))).rep())

    // position 3621-3690
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(BaseOps.Xor))).rep())

    // position 3694-3760
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(BaseOps.Or))).rep())

    // position 3765-3786
    init {
        expr.set(logicalOr)
    }

    // position 3791-3847
    val exprstmt = expr.then(context.text("=").then(expr.fold(toBinary(BaseOps.Assign))).opt())

    // position 3852-4068
    val whilestmt = context.text("while").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("body")).opt()).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4073-4308
    val forstmt = context.text("for").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4313-4355
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4360-4628
    val vardecl = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.fold(PutProperty("type"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "type", "init"))

    // position 4633-4886
    val parameter = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.fold(PutProperty("type"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "type")).or(idNode)

    // position 4891-4926
    val parameters = parameter.list(comma)

    // position 4931-5232
    val fundecl = context.text("fun").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5237-5529
    val classdecl = context.text("class").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 5534-5569
    val decl = vardecl.or(fundecl).or(classdecl)

    // position 5577-5607
    val stmtOrDecl = decl.or(stmt).then(context.text(";").opt())

    // position 5611-5651
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 5655-5679
    val program = stmts.then(toBlock)

}
