package at.searles.meelan

import at.searles.meelan.ops.*
import org.junit.Assert
import org.junit.Test

class VmGeneratorTest {
    @Test
    fun test() {
        val vm = VmGenerator.generateVm(listOf(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less))

        Assert.assertEquals("", vm)
    }
}