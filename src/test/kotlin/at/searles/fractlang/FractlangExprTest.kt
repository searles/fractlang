package at.searles.fractlang

import at.searles.commons.math.Cplx
import at.searles.fractlang.nodes.CplxNode
import at.searles.fractlang.nodes.IntNode
import at.searles.fractlang.semanticanalysis.SemanticAnalysisException
import org.junit.Assert
import org.junit.Test

class FractlangExprTest {
    @Test
    fun testAddition() {
        val node = FractlangExpr.fromString("1 + 1")
        Assert.assertEquals(2, (node as IntNode).value)
    }

    @Test
    fun testCplxWithOp() {
        val node = FractlangExpr.fromString("conj(-2:-2)")
        Assert.assertEquals(Cplx(-2.0, 2.0), (node as CplxNode).value)
    }

    @Test
    fun testUnknownSymbol() {
        try {
            FractlangExpr.fromString("conj(-2:a)")
            Assert.fail()
        } catch(e: SemanticAnalysisException) {
            e.printStackTrace()
        }
    }
}