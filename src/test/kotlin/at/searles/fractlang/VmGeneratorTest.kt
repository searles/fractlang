package at.searles.fractlang

import at.searles.fractlang.ops.*
import at.searles.fractlang.vm.VmGenerator
import org.junit.Assert
import org.junit.Test

class VmGeneratorTest {
    @Test
    fun test() {
        val vm = VmGenerator.generateVm(listOf(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less))

        Assert.assertEquals("", vm)
    }
}