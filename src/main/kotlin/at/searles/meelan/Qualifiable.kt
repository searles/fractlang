package at.searles.meelan

interface Qualifiable {
    fun qualify(qualifier: String): Node
}