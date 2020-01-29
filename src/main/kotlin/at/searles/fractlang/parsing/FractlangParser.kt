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
    // position 518-536
    val eof = context.parser(Regex.eof())

    // position 552-631
    val ws = context.parser(CharSet.interval(9, 13, 32, 32, 133, 133, 160, 160, 5760, 5760, 8192, 8202, 8232, 8233, 8239, 8239, 8287, 8287, 12288, 12288).plus())

    // position 645-671
    val mlComment = context.parser(Regex.text("/*").then(CharSet.interval(0, 2147483646).rep()).then(Regex.text("*/")).nonGreedy())

    // position 685-707
    val slComment = context.parser(Regex.text("//").then(CharSet.interval(0, 9, 11, 2147483646).rep()))

    // position 713-878
    init {
        tokenizer.addSkipped(ws.tokenId)
        tokenizer.addSkipped(slComment.tokenId)
        tokenizer.addSkipped(mlComment.tokenId)
    }


    // position 895-909
    val intRex: Regex = CharSet.interval(48, 57).plus()

    // position 926-954
    val hexRex: Regex = Regex.text("#").then(CharSet.interval(48, 57, 65, 70, 97, 102).range(1, 8))

    // position 971-990
    val decimals: Regex = Regex.text(".").then(CharSet.interval(48, 57).rep())

    // position 1007-1031
    val exponent: Regex = CharSet.interval(69, 69, 101, 101).then(Regex.text("-").opt()).then(CharSet.interval(48, 57).plus())

    // position 1048-1105
    val realRex: Regex = CharSet.interval(48, 57).plus().then(decimals.or(exponent).or(decimals.then(exponent)))

    // position 1122-1159
    val identifierRex: Regex = CharSet.interval(65, 90, 95, 95, 97, 122).then(CharSet.interval(48, 57, 65, 90, 95, 95, 97, 122).rep())

    // position 1176-1215
    val stringRex: Regex = Regex.text("\"").then(CharSet.interval(0, 33, 35, 91, 93, 2147483646).or(Regex.text("\\").then(CharSet.interval(0, 2147483646))).rep()).then(Regex.text("\""))

    // position 1225-1255
    val intNum = context.parser(intRex, toInt)

    // position 1263-1296
    val realNum = context.parser(realRex, toReal)

    // position 1304-1334
    val hexNum = context.parser(hexRex, toHex)

    // position 1342-1378
    val str = context.parser(stringRex, toEscString)

    // position 1386-1402
    val boolTrue = context.text("true")

    // position 1410-1428
    val boolFalse = context.text("false")

    // position 1436-1493
    val bool = boolTrue.then(toBool(true)).or(boolFalse.then(toBool(false)))

    // position 1501-1547
    val identifier = context.parser(identifierRex, toIdString)

    // position 1557-1595
    val intNode = intNum.or(hexNum).then(toIntNode)

    // position 1603-1633
    val realNode = realNum.then(toRealNode)

    // position 1641-1671
    val stringNode = str.then(toStringNode)

    // position 1679-1708
    val idNode = identifier.then(toIdNode)

    // position 1716-1743
    val boolNode = bool.then(toBoolNode)

    // position 1753-1931
    val atom = intNode.annotate(Annot.Num).or(realNode.annotate(Annot.Num)).or(stringNode.annotate(Annot.Str)).or(boolNode.annotate(Annot.Num)).or(idNode.annotate(Annot.Id))

    // position 1947-2025
    val qualifier = context.text(".").then(identifier.fold(toQualified)).or(context.text("[").then(expr.fold(toIndexed)).then(context.text("]")))

    // position 2040-2066
    val comma = context.text(",").annotate(Annot.Comma)

    // position 2074-2102
    val exprList = expr.list(comma)

    // position 2109-2256
    val appHead = atom.or(context.text("[").then(exprList.annotate(Annot.Intent)).then(toVectorNode).then(context.text("]"))).or(context.text("|").then(expr).then(toUnary(Abs)).then(context.text("|"))).or(context.text("(").then(expr).then(context.text(")"))).then(qualifier.rep())

    // position 2263-2293
    val qualifiedAtom = atom.then(qualifier.rep())

    // position 2303-2378
    val appArgument = qualifiedAtom.then(CreateSingletonList()).or(context.text("(").then(exprList).then(context.text(")")))

    // position 2407-2490
    val app = appHead.then(appArgument.then(appArgument.fold(listApply).rep()).fold(toApp).opt()).then(qualifier.rep())

    // position 2500-2971
    init {
        ifExpr.set(context.text("if").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.fold(PutProperty("thenBranch"))).then(context.text("else").annotate(Annot.Keyword).then(stmt.fold(PutProperty("elseBranch"))).then(CreateObject<Node>(IfElse::class.java, true, "condition", "thenBranch", "elseBranch")).or(CreateObject<Node>(If::class.java, true, "condition", "thenBranch"))))
    }

    // position 2978-3061
    val block = context.text("{").annotate(Annot.Newline).then(stmts.annotate(Annot.Intent)).then(toBlock.annotate(Annot.Newline)).then(context.text("}"))

    // position 3071-3097
    val term = ifExpr.or(block).or(app)

    // position 3147-3274
    init {
        literal.set(context.text("-").then(literal).then(toUnary(Neg)).or(context.text("/").then(literal).then(toUnary(Recip))).or(term))
    }

    // position 3308-3356
    val cons = literal.then(context.text(":").then(literal.fold(toBinary(Cons))).opt())

    // position 3366-3411
    init {
        pow.set(cons.then(context.text("^").then(pow.fold(toBinary(Pow))).opt()))
    }

    // position 3421-3523
    val product = pow.then(context.text("*").then(pow.fold(toBinary(Mul))).or(context.text("/").then(pow.fold(toBinary(Div)))).or(context.text("%").then(pow.fold(toBinary(Mod)))).rep())

    // position 3533-3613
    val sum = product.then(context.text("+").then(product.fold(toBinary(Add))).or(context.text("-").then(product.fold(toBinary(Sub)))).rep())

    // position 3620-3858
    val cmp = sum.then(context.text(">").then(sum.fold(toBinary(Greater))).or(context.text(">=").then(sum.fold(toBinary(GreaterEqual)))).or(context.text("<=").then(sum.fold(toBinary(LessEqual)))).or(context.text("<").then(sum.fold(toBinary(Less)))).or(context.text("==").then(sum.fold(toBinary(Equal)))).or(context.text("!=").then(sum.fold(toBinary(NotEqual)))).opt())

    // position 3865-3907
    val logicalLit = context.text("not").then(cmp).then(toUnary(Not)).or(cmp)

    // position 3912-3973
    val logicalAnd = logicalLit.then(context.text("and").then(logicalLit.fold(toBinary(And))).rep())

    // position 3978-4039
    val logicalXor = logicalAnd.then(context.text("xor").then(logicalAnd.fold(toBinary(Xor))).rep())

    // position 4044-4102
    val logicalOr = logicalXor.then(context.text("or").then(logicalXor.fold(toBinary(Or))).rep())

    // position 4109-4130
    init {
        expr.set(logicalOr)
    }

    // position 4137-4181
    val exprstmt = expr.then(context.text("=").then(expr.fold(toAssignment)).opt())

    // position 4188-4433
    val whilestmt = context.text("while").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(expr.fold(PutProperty("condition"))).then(context.text(")")).then(stmt.or(createNop, true).fold(PutProperty("body"))).then(CreateObject<Node>(While::class.java, true, "condition", "body"))

    // position 4440-4681
    val forstmt = context.text("for").annotate(Annot.Keyword).then(CreateEmptyProperties).then(context.text("(")).then(identifier.fold(PutProperty("name"))).then(context.text("in")).then(expr.fold(PutProperty("range"))).then(context.text(")")).then(stmt.fold(PutProperty("body"))).then(CreateObject<Node>(For::class.java, true, "name", "range", "body"))

    // position 4688-4730
    init {
        stmt.set(whilestmt.or(forstmt).or(exprstmt))
    }

    // position 4737-5030
    val vardecl = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(context.text("=").then(expr.fold(PutProperty("init"))).opt()).then(CreateObject<Node>(VarDecl::class.java, true, "name", "varType", "init"))

    // position 5037-5256
    val valdecl = context.text("val").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("=").then(expr.fold(PutProperty("init")))).then(CreateObject<Node>(ValDecl::class.java, true, "name", "init"))

    // position 5263-5540
    val parameter = context.text("var").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(identifier.then(ToType).fold(PutProperty("varType"))).opt()).then(CreateObject<Node>(VarParameter::class.java, true, "name", "varType")).or(idNode)

    // position 5547-5582
    val parameters = parameter.list(comma)

    // position 5589-5874
    val fundecl = context.text("fun").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(")).then(parameters.fold(PutProperty("parameters"))).then(context.text(")")).then(block.or(context.text("=").then(expr)).fold(PutProperty("body"))).then(CreateObject<Node>(FunDecl::class.java, true, "name", "parameters", "body"))

    // position 5881-6180
    val classdecl = context.text("class").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text("(").then(parameters).then(context.text(")")).or(CreateEmptyList()).fold(PutProperty("parameters"))).then(block.fold(PutProperty("body"))).then(CreateObject<Node>(ClassDecl::class.java, true, "name", "parameters", "body"))

    // position 6187-6462
    val externdecl = context.text("extern").annotate(Annot.DefKeyword).then(CreateEmptyProperties).then(identifier.fold(PutProperty("name"))).then(context.text(":").then(expr.fold(PutProperty("description"))).opt()).then(context.text("=")).then(str.fold(PutProperty("expr"))).then(CreateObject<Node>(ExternDecl::class.java, true, "name", "description", "expr"))

    // position 6469-6527
    val decl = vardecl.or(valdecl).or(fundecl).or(classdecl).or(externdecl)

    // position 6537-6578
    val semicolon = context.text(";").then(Mapping.identity<Node>())

    // position 6588-6664
    val stmtOrDecl = decl.or(stmt).then(semicolon.or(SkipSemicolon, true).annotate(Annot.Stmt))

    // position 6668-6708
    init {
        stmts.set(stmtOrDecl.list())
    }

    // position 6713-6737
    val program = stmts.then(toBlock)

}
