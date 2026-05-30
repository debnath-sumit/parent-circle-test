package com.parentcircle.pages.components.navigation;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class NavigationComponentLocators {

    private final Page page;

    public NavigationComponentLocators(Page page) {
        this.page = page;
    }

    private Locator nav() {
        return page.getByRole(AriaRole.NAVIGATION);
    }

    public Locator signInLink() {
        return nav().getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Sign in"));
    }

    public Locator signOutButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign out"));
    }

    public Locator profileLink() {
        // exact=true: a non-exact "Profile" matches multiple links (strict-mode violation).
        // Page-scoped, not nav()-scoped — the link sits outside the <nav> landmark.
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile").setExact(true));
    }
}
