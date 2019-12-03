package generators

import at.searles.fractlang.ops.*
import at.searles.fractlang.vm.VmGenerator
import org.junit.Test
import java.io.File

class GenerateVm {
    @Test
    fun test() {
        if(!createFiles) {
            return
        }

        val vm = VmGenerator.generateVm(listOf(Add, Sub, Mul, Div, Mod, Neg, Assign, Jump, Equal, Less))

        File("src/test/resources/fractlang_vm.c").writeText(vm)
    }

    companion object {
        const val createFiles = false // switch to create parser-file
    }
}