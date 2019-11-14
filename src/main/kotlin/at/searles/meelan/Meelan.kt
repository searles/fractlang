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
    // position 463-522
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 532-589
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 645-671
    val slComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 684-716
    val mlComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).or(CharSet.interval(0, 12, 14, 2147483646)).rep()))

    // position 720-921
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 936-954
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 970-998
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 1014-1033
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 1049-1073
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1089-1145
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1161-1198
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1214-1253
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1261-1291
    val intNum = context.parser(intRex, toInt)

    // position 1298-1331
    val realNum = context.parser(realRex, toReal)

    // position 1338-1368
    val hexNum = context.parser(hexRex, toHex)

    // position 1375-1411
    val str = context.parser(stringRex, toEscString)

    // position 1418-1457
    val bool = context.parser(Regex.text("true").or(Regex.text("false")), toBool)

    // position 1464-1510
    val identifier = context.parser(identifierRex, toIdString)

    // position 1518-1556
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1563-1593
    val realNode = realNum.then(toRealNode)

    // position 1600-1630
    val stringNode = str.then(toStringNode)

    // position 1637-1666
    val idNode = identifier.then(toIdNode)

    // position 1674-1700
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1707-1735
    val exprList = expr.list(comma)

    // position 1743-1786
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1794-1976
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 1984-2034
    val qualified = atom.then(context.text(".").then(identifier.fold(toQualified)).rep())

    // position 2042-2104
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2111-2154
    val singleArgument = app.then(CreateSingletonList())

    // position 2161-2221
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2277-2321
    init {
        app.set(qualified.then(arguments.fold(toApp).opt()))
    }

    // position 2368-2822
    init {
        ifExpr.set(context.text("if").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Kw).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2827-2857
    val block = context.text("{").then(stmts).then(toBlock).then(context.text("}"))

    // position 2862-2898
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 2906-2942
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 2950-3075
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3106-3154
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3162-3207
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3215-3317
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3325-3405
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3410-3641
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3646-3688
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3692-3753
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3757-3818
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 3822-3880
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 3885-3906
    init {
        expr.set(logicalOr)
    }

    // position 3911-3959
    val exprstmt = expr.then(context.text("=").then(expr.fold(toBinary(Assign))).opt())

    // position 3964-4189
    val whilestmt = context.text("while").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(toNop).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4194-4429
    val forstmt = context.text("for").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4434-4476
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4481-4766
    val vardecl = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 4771-4983
    val valdecl = context.text("val").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 4988-5258
    val parameter = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5263-5298
    val parameters = parameter.list(comma)

    // position 5303-5604
    val fundecl = context.text("fun").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5609-5901
    val classdecl = context.text("class").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 5906-5951
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl)

    // position 5959-6000
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6008-6069
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true))

    // position 6073-6113
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6117-6141
    val program = stmts.then(toBlock)

}
