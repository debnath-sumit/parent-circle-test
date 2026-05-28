package com.parentcircle.tests;

import com.parentcircle.base.BaseTest;
import com.parentcircle.pages.home.HomePage;
import com.parentcircle.pages.signin.SignInPage;
import com.parentcircle.utils.TestUser;
import com.parentcircle.utils.TestUsers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class SignInTests extends BaseTest {

    @Test
    @DisplayName("user can sign in and then sign out")
    void signInAndSignOut() {
        TestUser user = TestUsers.standard();

        HomePage home = new HomePage(page).open(baseUrl);
        SignInPage signIn = home.goToSignIn();

        assertThat(signIn.locators().emailInput()).isVisible();
        assertThat(signIn.locators().passwordInput()).isVisible();

        signIn.signIn(user);

        assertThat(home.navigation().locators().signOutButton()).isVisible();

        home.navigation().clickSignOut();
        page.navigate(baseUrl);

        assertThat(home.navigation().locators().signInLink()).isVisible();
    }
}
