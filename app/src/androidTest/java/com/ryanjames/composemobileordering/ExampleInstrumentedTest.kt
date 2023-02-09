package com.ryanjames.composemobileordering

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ryanjames.composemobileordering.features.login.LoginActivity
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalMaterialApi
@ExperimentalPagerApi
class ExampleInstrumentedTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule(LoginActivity::class.java)
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun useAppContext() {
        composeTestRule.onNodeWithText("Username").performTextInput("testcedar")
        composeTestRule.onNodeWithText("Password").performTextInput("james")
        assertEquals("Sign In", composeTestRule.onNodeWithTag("btnSignIn").getText())
    }

    private fun SemanticsNodeInteraction.getText(): String {
        return this.fetchSemanticsNode().config[SemanticsProperties.Text][0].toString()
    }

//    Failed to assert the following: (Text + EditableText = [Sign In 2])
//    Semantics of the node:
//    Node #14 at (l=112.0, t=1917.0, r=1328.0, b=2088.0)px, Tag: 'btnSignIn'
//    Role = 'Button'
//    Focused = 'false'
//    Text = '[Sign In]'
//    Actions = [OnClick, GetTextLayoutResult]
//    MergeDescendants = 'true'
//    Has 6 siblings
//    Selector used: (TestTag = 'btnSignIn')
//

}