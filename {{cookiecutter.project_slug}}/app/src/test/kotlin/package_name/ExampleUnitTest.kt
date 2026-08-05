package {{ cookiecutter.application_id }}

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `greetingText formats the given name`() {
        assertEquals("Hello, World", greetingText("World"))
    }

    @Test
    fun `greetingText can be exercised through a mocked collaborator`() {
        val nameProvider = mockk<NameProvider>()
        every { nameProvider.name() } returns "Mock"

        assertEquals("Hello, Mock", greetingText(nameProvider.name()))
    }

    private interface NameProvider {
        fun name(): String
    }
}
