package com.parentcircle.pages.profile;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Locators only. No actions, no clicks, no fills.
 * If a selector changes in the UI, this is the single file to edit.
 *
 * Accessible names verified against the live Add Child form (the "*" is part of
 * the field's accessible name). The DOB field is a plain textbox that accepts an
 * ISO date string (e.g. "2023-03-03").
 */
public class ProfilePageLocators {

    private final Page page;

    public ProfilePageLocators(Page page) {
        this.page = page;
    }

    public Locator childNameInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Child name *"));
    }

    public Locator dobInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Date of birth *"));
    }

    public Locator addChildButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add child"));
    }
    public Locator removeChildButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove"));
    }

    /** The added child's entry in the children list, matched by name. */
    public Locator childEntry(String name) {
        return page.getByText(name);
    }
}
