package com.parentcircle.pages.profile;

import com.microsoft.playwright.Page;
import com.parentcircle.pages.BasePage;

/**
 * Actions for the profile screen. Holds no selectors of its own — defers to
 * {@link ProfilePageLocators} so element queries and user actions stay separated.
 */
public class ProfilePage extends BasePage {

    private final ProfilePageLocators loc;

    public ProfilePage(Page page) {
        super(page);
        this.loc = new ProfilePageLocators(page);
    }

    public ProfilePageLocators locators() {
        return loc;
    }

    public void addChild(String name, String dateOfBirth) {
        loc.childNameInput().fill(name);
        loc.dobInput().fill(dateOfBirth);
        loc.addChildButton().click();
    }
}
