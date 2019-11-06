import at.searles.buf.ReaderCharStream
import at.searles.lexer.TokenStream
import at.searles.parsing.ParserStream
import at.searles.parsingtools.generator.Generator
import at.searles.parsingtools.generator.KotlinVisitor
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileReader

class GenerateParser {

    // Set createFiles to true.

    @Test
    fun meelan() {
        if(!createFiles) {
            return
        }

        val input = ParserStream(TokenStream.fromCharStream(ReaderCharStream(FileReader("src/main/resources/Meelan.grammar"))))
        val output = Generator.program.parse(input)

        if(output is Generator.Program) {
            val kotlinSource = output.accept(KotlinVisitor())
            File("src/main/kotlin/at/searles/meelan/Meelan.kt").writeText(kotlinSource)
        } else {
            Assert.fail()
        }
    }

    companion object {
        val createFiles = false
    }
}