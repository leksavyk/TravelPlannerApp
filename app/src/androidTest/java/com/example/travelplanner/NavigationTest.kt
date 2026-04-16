package com.example.travelplanner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.travelplanner.ui.navigation.AppNavigation
import org.junit.Rule
import org.junit.Test


class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToProfile() {
        composeTestRule.setContent {
            AppNavigation()
        }
        composeTestRule.onNodeWithText("Profile").performClick()

        composeTestRule.onNodeWithText("Мій профіль").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oleksandra").assertIsDisplayed()
    }

    @Test
    fun testOpenTripDetails() {
        composeTestRule.setContent {
            AppNavigation()
        }
        composeTestRule.onNodeWithText("Вікенд у Києві").performClick()

        composeTestRule.onNodeWithText("Місця для відвідування:").assertIsDisplayed()
    }
}