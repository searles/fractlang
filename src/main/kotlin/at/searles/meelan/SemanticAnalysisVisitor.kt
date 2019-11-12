package at.searles.meelan

import at.searles.meelan.ops.Assign
import java.lang.IllegalArgumentException

class SemanticAnalysisVisitor(val frame: Frame): Visitor<Node> {
    override fun visit(reg: Reg): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(cplxNode: CplxNode): Node {
        return cplxNode
    }

    override fun visit(intNode: IntNode): Node  {
        return intNode
    }


    override fun visit(realNode: RealNode): Node  {
        return realNode
    }

    override fun visit(stringNode: StringNode): Node  {
        return stringNode
    }

    override fun visit(boolNode: BoolNode): Node  {
        return boolNode
    }

    override fun visit(block: Block): Node  {
        val subframe = frame.createSubFrame()
        val subvisitor = SemanticAnalysisVisitor(subframe)
        block.stmts.forEach { it.accept(subvisitor)?.let { subframe.addStmt(it) } }
        return subframe
    }

    override fun visit(classDecl: ClassDecl): Node  {
        frame.addClass(classDecl)
        return NopNode(classDecl.trace)
    }

    override fun visit(funDecl: FunDecl): Node  {
        frame.addFun(funDecl)
        return NopNode(funDecl.trace)
    }

    override fun visit(varDecl: VarDecl): Node  {
        val initialization = varDecl.init?.accept(this)

        val type = varDecl.typeName
            ?.let{ typeName ->
                BaseTypes.values()
                    .firstOrNull { it.name == typeName }
                    ?: throw SemanticAnalysisException("unknown type $typeName", varDecl) }
            ?:initialization?.type
            ?:throw SemanticAnalysisException("missing type", varDecl)

        val reg = frame.addVar(varDecl.trace, varDecl.name, type)

        if(initialization != null) {
            Instruction(varDecl.trace, Assign, reg, type.convert(initialization))
        }

        return NopNode(varDecl.trace)
    }

    override fun visit(idNode: IdNode): Node  {
        val image = frame.get(idNode.id)

        if(image != null) {
            return image.accept(this)
        }

        // TODO: if id is a function, then...

        // otherwise add it

        throw SemanticAnalysisException("${idNode.id} undefied", idNode)
    }

    override fun visit(instruction: Instruction): Node  {
        // FIXME think about derivatives

        val typedArgs = instruction.arguments.map { it.accept(this) }

        // TODO signature should be inside BaseOp
        val matchingSignature = instruction.op.findSignature(typedArgs)
            ?: throw SemanticAnalysisException("no matching signature", instruction)

        return instruction.op.apply(instruction.trace, typedArgs)
    }

    override fun visit(qualifiedNode: QualifiedNode): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(app: App): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(classEnv: ClassEnv): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funEnv: FunEnv): Node  {
        // this is a constant.
        return funEnv.accept(AppVisitor(frame, emptyList()))
    }


    override fun visit(forStmt: For): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifStmt: If): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifElse: IfElse): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varParameter: VarParameter): Node  {
        throw IllegalArgumentException("unexpected varParameter")
    }

    override fun visit(vectorNode: VectorNode): Node  {
        // TODO: Fix null check
        return VectorNode(vectorNode.trace, vectorNode.items.map { it.accept(this)!! })
    }

    override fun visit(whileStmt: While): Node  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(frame: Frame): Node  {
        throw IllegalArgumentException("frame is already semantically analyzed")
    }

}