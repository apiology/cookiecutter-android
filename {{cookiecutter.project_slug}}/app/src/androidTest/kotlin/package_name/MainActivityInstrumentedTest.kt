package {{ cookiecutter.application_id }}

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun greetingIsDisplayed() {
        composeTestRule.onNodeWithText("Hello, {{ cookiecutter.project_name }}").assertExists()
    }
}
