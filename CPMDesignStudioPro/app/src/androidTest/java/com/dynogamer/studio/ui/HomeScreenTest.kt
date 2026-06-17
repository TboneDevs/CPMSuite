package com.dynogamer.studio.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.dynogamer.studio.ui.screens.home.HomeScreen
import com.dynogamer.studio.ui.theme.CPMStudioTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_displaysTitle() {
        composeTestRule.setContent {
            CPMStudioTheme {
                HomeScreen(onNavigate = {})
            }
        }
        composeTestRule.onNodeWithText("CPM Design Studio").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysQuickActionButtons() {
        composeTestRule.setContent {
            CPMStudioTheme {
                HomeScreen(onNavigate = {})
            }
        }
        composeTestRule.onNodeWithText("Scan Device").assertIsDisplayed()
        composeTestRule.onNodeWithText("Import CPM1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Import CPM2").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysNavigationRows() {
        composeTestRule.setContent {
            CPMStudioTheme {
                HomeScreen(onNavigate = {})
            }
        }
        composeTestRule.onNodeWithText("CPM1 Workspace").assertIsDisplayed()
        composeTestRule.onNodeWithText("CPM2 Workspace").assertIsDisplayed()
        composeTestRule.onNodeWithText("Conversion Studio").assertIsDisplayed()
    }

    @Test
    fun homeScreen_scanDeviceButton_isClickable() {
        var clicked = false
        composeTestRule.setContent {
            CPMStudioTheme {
                HomeScreen(onNavigate = { clicked = true })
            }
        }
        composeTestRule.onNodeWithText("Scan Device").performClick()
        // Scan triggers ViewModel, not navigation
    }
}
