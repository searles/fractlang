package at.searles.meelan

class SemanticAnalysisVisitor(frame: Frame): Visitor<Node?> {
    override fun visit(app: App): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(block: Block): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(classDecl: ClassDecl): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(defDecl: DefDecl): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(forStmt: For): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(frame: Frame): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(funDecl: FunDecl): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(idNode: IdNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifStmt: If): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(ifElse: IfElse): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(intNode: IntNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(qualifiedNode: QualifiedNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(realNode: RealNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(stringNode: StringNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varDecl: VarDecl): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(varParameter: VarParameter): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(vectorNode: VectorNode): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(whileStmt: While): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    
    internal class AppVisitor(args: List<Node>): Visitor<Node?> {
        override fun visit(app: App): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(block: Block): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(classDecl: ClassDecl): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(defDecl: DefDecl): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(forStmt: For): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(frame: Frame): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(funDecl: FunDecl): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(idNode: IdNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(ifStmt: If): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(ifElse: IfElse): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(intNode: IntNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(qualifiedNode: QualifiedNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(realNode: RealNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(stringNode: StringNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(varDecl: VarDecl): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(varParameter: VarParameter): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(vectorNode: VectorNode): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(whileStmt: While): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}