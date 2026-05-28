package com.parentcircle.pages.signin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Locators only. No actions, no clicks, no fills.
 * If a selector changes in the UI, this is the single file to edit.
 */
public class SignInPageLocators {

    private final Page page;

    public SignInPageLocators(Page page) {
        this.page = page;
    }

    public Locator emailInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email"));
    }

    public Locator passwordInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
    }

    public Locator signInButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in"));
    }
}
