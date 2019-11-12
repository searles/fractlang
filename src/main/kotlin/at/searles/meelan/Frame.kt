package at.searles.meelan

import at.searles.parsing.Trace

class Frame(trace: Trace, val parent: SymbolTable): SymbolTable, Node(trace) {
    var byteSize = 0
        private set

    val table = HashMap<String, Node>()
    val registers = ArrayList<Reg>()

    val instructions = ArrayList<Node>()

    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }

    private fun allocate(trace: Trace, type: Type): Reg {
        return Reg(trace, type, byteSize).also {
            registers.add(it)
            byteSize += type.byteSize()
        }
    }

    fun addStmt(node: Node) {
        instructions.add(node)
    }

    fun addFun(function: FunDecl) {
        // function definition cannot leave the scope of this fun. Thus, only preserve top of
        // hashmap. Would be a problem anyways because of registers.

        // should I simply use the frame right away?
        // for starters, why not...

        if(table.put(function.name, FunEnv(function, this)) != null) {
            throw SemanticAnalysisException("duplicate declaration in top-scope", function)
        }
    }

    fun addClass(clazz: ClassDecl) {
        if(table.put(clazz.name, ClassEnv(clazz, this)) != null) {
            throw SemanticAnalysisException("duplicate declaration in top-scope", clazz)
        }
    }

    fun addExtern() {
        // TODO
    }

    fun addVar(trace: Trace, id: String, type: Type): Reg {
        val reg = allocate(trace, type)
        table[id] = reg
        return reg
    }

    fun createSubFrame(trace: Trace): Frame {
        return Frame(trace, this)
    }

    override fun get(id: String): Node? {
        return table[id]?:parent[id]
    }
}