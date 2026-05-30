package com.parentcircle.pages.components.navigation;

import com.microsoft.playwright.Page;

public class NavigationComponent {

    private final NavigationComponentLocators loc;

    public NavigationComponent(Page page) {
        this.loc = new NavigationComponentLocators(page);
    }

    public NavigationComponentLocators locators() {
        return loc;
    }

    public void clickSignIn() {
        loc.signInLink().click();
    }

    public void clickSignOut() {
        loc.signOutButton().click();
    }

    public void clickProfile() {
        loc.profileLink().click();
    }
}
