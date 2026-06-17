package com.dynogamer.studio.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun bottomNav_homeIsSelectedByDefault() {
        composeTestRule.onNodeWithText("CPM Design Studio").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigateToCpm1() {
        composeTestRule.onNodeWithText("CPM1").performClick()
        composeTestRule.onNodeWithText("CPM1 Workspace").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigateToCpm2() {
        composeTestRule.onNodeWithText("CPM2").performClick()
        composeTestRule.onNodeWithText("CPM2 Workspace").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigateToProjects() {
        composeTestRule.onNodeWithText("Projects").performClick()
        composeTestRule.onNodeWithText("Project Library").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigateToSettings() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }
}
