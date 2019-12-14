package generators

import at.searles.fractlang.ops.*
import at.searles.fractlang.vm.VmGenerator
import org.junit.Test
import java.io.File

class GenerateVm {
    @Test
    fun test() {
        val vm = VmGenerator.generateVm(listOf(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less, Point, SetResult))

        File("generated/vm.rsh").writeText(vm)
    }
}