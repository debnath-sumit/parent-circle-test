package com.parentcircle.tests;

import com.parentcircle.base.BaseTest;
import com.parentcircle.pages.home.HomePage;
import com.parentcircle.pages.profile.ProfilePage;
import com.parentcircle.utils.TestUser;
import com.parentcircle.utils.TestUsers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class AddChildTests extends BaseTest {

    private static final String CHILD_NAME = "Test data";
    // Intended display: "March 03, 2023". Adjust the format to match the DOB widget —
    // ISO for an <input type="date">, or a localized string for a free-text field.
    private static final String CHILD_DOB = "2023-03-03";

    @Test
    @DisplayName("signed-in user can add a child from the profile page, then sign out")
    void addChildFromProfile() {
        TestUser user = TestUsers.standard();

        HomePage home = new HomePage(page).open(baseUrl);
        home.goToSignIn().signIn(user);

        ProfilePage profile = home.goToProfile();
        profile.addChild(CHILD_NAME, CHILD_DOB);

        // child was added to the list
        assertThat(profile.locators().childEntry(CHILD_NAME)).isVisible();

        // sign out and confirm we're signed out
        home.navigation().clickSignOut();
        page.navigate(baseUrl);
        assertThat(home.navigation().locators().signInLink()).isVisible();
    }
}
