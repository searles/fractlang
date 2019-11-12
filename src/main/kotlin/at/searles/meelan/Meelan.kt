/* This file is generated */
package at.searles.meelan;

import at.searles.parsing.Mapping
import at.searles.parsingtools.*
import at.searles.parsingtools.list.CreateEmptyList
import at.searles.parsingtools.list.CreateSingletonList
import at.searles.parsingtools.properties.CreateEmptyProperties
import at.searles.parsingtools.properties.CreateObject
import at.searles.parsingtools.properties.PutProperty

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.meelan.ops.*
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
    // position 411-470
    val ws = context.parser(CharSet.interval(9, 9, 11, 11, 13, 13, 32, 32, 160, 160, 5760, 5760, 8192, 8202, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 480-537
    val nl = context.parser(Regex.text("\r\n").or(Regex.text("\n")).or(Regex.text("\u000c")).or(Regex.text("\u0085")).or(Regex.text("\u2028")).or(Regex.text("\u2029")))

    // position 593-619
    val slComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 632-664
    val mlComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).or(CharSet.interval(0, 12, 14, 2147483646)).rep()))

    // position 668-869
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(nl.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 884-902
    val intRex: Regex = CharSet.interval(48, 57).range(1, 8)

    // position 918-946
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 962-981
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 997-1021
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1037-1093
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1109-1146
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1162-1201
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1209-1239
    val intNum = context.parser(intRex, toInt)

    // position 1246-1279
    val realNum = context.parser(realRex, toReal)

    // position 1286-1316
    val hexNum = context.parser(hexRex, toHex)

    // position 1323-1359
    val str = context.parser(stringRex, toEscString)

    // position 1366-1412
    val identifier = context.parser(identifierRex, toIdString)

    // position 1420-1458
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1465-1495
    val realNode = realNum.then(toRealNode)

    // position 1502-1532
    val stringNode = str.then(toStringNode)

    // position 1539-1568
    val idNode = identifier.then(toIdNode)

    // position 1576-1602
    val comma = context.text(",").annotate(Annot.Comma)

    // position 1609-1637
    val exprList = expr.list(comma)

    // position 1645-1688
    val vectorNode = context.text("[").then(exprList).then(toVectorNode).then(context.text("]"))

    // position 1696-1878
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(idNode.annotate(Annot.Id)).or(vectorNode).or(context.text("(").then(expr).then(context.text(")")))

    // position 1886-1936
    val qualified = atom.then(context.text(".").then(identifier.fold(toQualified)).rep())

    // position 1944-2006
    val argumentsInParentheses = context.text("(").then(exprList).then(context.text(")")).then(app.fold(listApply).opt())

    // position 2013-2056
    val singleArgument = app.then(CreateSingletonList())

    // position 2063-2123
    val arguments = argumentsInParentheses.or(singleArgument, true)

    // position 2179-2223
    init {
        app.set(qualified.then(arguments.fold(toApp).opt()))
    }

    // position 2270-2724
    init {
        ifExpr.set(context.text("if").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Kw).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2729-2759
    val block = context.text("{").then(stmts).then(toBlock).then(context.text("}"))

    // position 2764-2808
    val absExpr = context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))

    // position 2816-2852
    val term = ifExpr.or(block).or(absExpr).or(app)

    // position 2860-3001
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(
            Recip))).or(term))
    }

    // position 3032-3088
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3096-3149
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3157-3283
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(
        Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3291-3387
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(
        Sub)))).rep())

    // position 3392-3671
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(
        GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(
        Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(
        NotEqual)))).opt())

    // position 3676-3726
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3730-3799
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3803-3872
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 3876-3942
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 3947-3968
    init {
        expr.set(logicalOr)
    }

    // position 3973-4029
    val exprstmt = expr.then(context.text("=").then(expr.fold(toBinary(Assign))).opt())

    // position 4034-4250
    val whilestmt = context.text("while").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("body")).opt()).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4255-4490
    val forstmt = context.text("for").annotate(Annot.Kw).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4495-4537
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4542-4818
    val vardecl = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.fold(PutProperty("typeName"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "typeName", "init"))

    // position 4823-5084
    val parameter = context.text("var").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.fold(PutProperty("typeName"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "typeName")).or(idNode)

    // position 5089-5124
    val parameters = parameter.list(comma)

    // position 5129-5430
    val fundecl = context.text("fun").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5435-5727
    val classdecl = context.text("class").annotate(Annot.DeclKw).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 5732-5767
    val decl = vardecl.or(fundecl).or(classdecl)

    // position 5775-5816
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 5824-5885
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true))

    // position 5889-5929
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 5933-5957
    val program = stmts.then(toBlock)

}
