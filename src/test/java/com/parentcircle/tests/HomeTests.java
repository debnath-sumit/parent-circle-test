package com.parentcircle.tests;

import com.parentcircle.base.BaseTest;
import com.parentcircle.pages.home.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class HomeTests extends BaseTest {

    @Test
    @DisplayName("home page loads and shows sign-in entry point")
    void homeShowsSignIn() {
        HomePage home = new HomePage(page).open(baseUrl);
        assertThat(home.navigation().locators().signInLink()).isVisible();
    }
}
