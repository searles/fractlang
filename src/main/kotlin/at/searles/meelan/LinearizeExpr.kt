package at.searles.meelan

import at.searles.meelan.nodes.*
import at.searles.meelan.ops.Assign
import java.lang.IllegalArgumentException

class LinearizeExpr(val stmts: ArrayList<Node>, val varNameGenerator: Iterator<String>): Visitor<Node> {
    override fun visit(app: App): Node {
        val linearizedArgs = app.args.map { it.accept(LinearizeExpr(stmts, varNameGenerator))}

        val resultVarName = varNameGenerator.next()

        val resultNode = IdNode(app.trace, resultVarName)

        stmts.add(App(app.trace, Assign, listOf(resultNode, App(app.trace, app.head, linearizedArgs))))
        stmts.add(VarDecl(app.trace, resultVarName, app.type, null))

        return resultNode
    }

    override fun visit(block: Block): Node {
        block.stmts.dropLast(1).forEach {
            it.accept(LinearizeStmt(stmts, varNameGenerator))
        }

        return block.stmts.last().accept(this)
    }

    override fun visit(idNode: IdNode): Node {
        return idNode
    }

    override fun visit(intNode: IntNode): Node {
        return intNode
    }

    override fun visit(realNode: RealNode): Node {
        return realNode
    }

    override fun visit(cplxNode: CplxNode): Node {
        return cplxNode
    }

    override fun visit(boolNode: BoolNode): Node {
        throw IllegalArgumentException()
    }

    override fun visit(varDecl: VarDecl): Node {
        require(varDecl.init == null)
        return varDecl
    }

    override fun visit(ifElse: IfElse): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(forStmt: For): Node {
        throw IllegalArgumentException()
    }

    override fun visit(ifStmt: If): Node {
        throw IllegalArgumentException()
    }

    override fun visit(whileStmt: While): Node {
        throw IllegalArgumentException() // should be a type error.
    }

    override fun visit(classEnv: ClassEnv): Node {
        throw IllegalArgumentException()
    }

    override fun visit(funEnv: FunEnv): Node {
        throw IllegalArgumentException()
    }

    override fun visit(opNode: OpNode): Node {
        throw IllegalArgumentException()
    }

    override fun visit(objectNode: ObjectNode): Node {
        throw IllegalArgumentException()
    }

    override fun visit(valDecl: ValDecl): Node {
        throw IllegalArgumentException()
    }

    override fun visit(nop: Nop): Node {
        throw IllegalArgumentException()
    }

    override fun visit(classDecl: ClassDecl): Node {
        throw IllegalArgumentException()
    }

    override fun visit(funDecl: FunDecl): Node {
        throw IllegalArgumentException()
    }

    override fun visit(qualifiedNode: QualifiedNode): Node {
        throw IllegalArgumentException()
    }

    override fun visit(stringNode: StringNode): Node {
        throw IllegalArgumentException()
    }

    override fun visit(varParameter: VarParameter): Node {
        throw IllegalArgumentException()
    }

    override fun visit(vectorNode: VectorNode): Node {
        throw IllegalArgumentException()
    }
}
