package com.example.momentary

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.momentary.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogoutMechanism() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("onboarding_pager")
                .fetchSemanticsNodes().isNotEmpty()
        }
        repeat(3) {
            composeTestRule.onNodeWithTag("onboarding_pager").performTouchInput {
                swipeLeft()
            }
            composeTestRule.waitForIdle()
        }
        composeTestRule.onNodeWithTag("onboarding_start_button").performClick()

        /** Login Scenario */
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("email_input")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("email_input").performTextInput("m_actor@dicoding.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("password")
        composeTestRule.onNodeWithTag("login_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("stories_list")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("stories_list").assertIsDisplayed()

        /** AddStory Scenario */
        composeTestRule.onNodeWithTag("add_story").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("image_picker_box")
                .fetchSemanticsNodes().isNotEmpty()
        }

        Intents.init()
        val mockImageUri = createMockImageUri()
        // Stubbing intent gallery
        val galleryResultData = Intent().apply {
            data = mockImageUri
        }
        val galleryResult = Instrumentation.ActivityResult(Activity.RESULT_OK, galleryResultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(galleryResult)
        // Stubbing intent UCrop
        val uCropResultData = Intent().apply {
            putExtra(com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI, mockImageUri)
        }
        val uCropResult = Instrumentation.ActivityResult(Activity.RESULT_OK, uCropResultData)
        intending(org.hamcrest.Matchers.not(hasAction(Intent.ACTION_GET_CONTENT)))
            .respondWith(uCropResult)

        composeTestRule.onNodeWithTag("image_picker_box").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag("gallery_button")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("gallery_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("description_input")
            .performTextInput("building exceptional experiences through code.")
        composeTestRule.onNodeWithTag("upload_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("stories_list")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("stories_list").assertIsDisplayed()
        Intents.release()

        /** Logout Scenario */
        composeTestRule.onNodeWithTag("drawer_menu_button").performClick()
        composeTestRule.onNodeWithTag("logout_menu_item").performClick()
        composeTestRule.onNodeWithTag("log_out").performClick()
        composeTestRule.waitForIdle()

        // Make sure logout successfully
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("email_input")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("email_input").assertIsDisplayed()
    }

    private fun createMockImageUri(): Uri {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val targetFile = File(context.cacheDir, "mock_upload_image.jpg")

        testContext.assets.open("test_image.jpg").use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return Uri.fromFile(targetFile)
    }
}
