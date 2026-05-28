package com.parentcircle.pages.signin;

import com.microsoft.playwright.Page;
import com.parentcircle.pages.BasePage;
import com.parentcircle.utils.TestUser;

/**
 * Actions for the sign-in screen. Holds no selectors of its own — defers
 * to {@link SignInPageLocators} so element queries and user actions are
 * separated.
 */
public class SignInPage extends BasePage {

    private final SignInPageLocators loc;

    public SignInPage(Page page) {
        super(page);
        this.loc = new SignInPageLocators(page);
    }

    public SignInPageLocators locators() {
        return loc;
    }

    public void signIn(TestUser user) {
        signIn(user.email(), user.password());
    }

    public void signIn(String email, String password) {
        loc.emailInput().fill(email);
        loc.passwordInput().fill(password);
        loc.signInButton().click();
    }
}
