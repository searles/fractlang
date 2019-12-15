package generators

import at.searles.fractlang.CompilerInstance
import at.searles.fractlang.ops.*
import at.searles.fractlang.vm.VmGenerator
import org.junit.Test
import java.io.File

class GenerateVm {
    @Test
    fun test() {
        val vm = VmGenerator.generateVm(CompilerInstance.vmInstructions)

        File("generated/vm.rsh").writeText(vm)
    }
}