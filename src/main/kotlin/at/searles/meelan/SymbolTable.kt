package at.searles.meelan

interface SymbolTable {
    operator fun get(id: String): Node?
}
