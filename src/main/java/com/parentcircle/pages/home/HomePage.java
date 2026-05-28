package com.parentcircle.pages.home;

import com.microsoft.playwright.Page;
import com.parentcircle.pages.BasePage;
import com.parentcircle.pages.components.navigation.NavigationComponent;
import com.parentcircle.pages.signin.SignInPage;

/**
 * Landing page. Holds no locators of its own — composes the shared
 * navigation component and exposes high-level navigation actions.
 */
public class HomePage extends BasePage {

    private final NavigationComponent navigation;

    public HomePage(Page page) {
        super(page);
        this.navigation = new NavigationComponent(page);
    }

    public HomePage open(String baseUrl) {
        page.navigate(baseUrl);
        return this;
    }

    public NavigationComponent navigation() {
        return navigation;
    }

    public SignInPage goToSignIn() {
        navigation.clickSignIn();
        return new SignInPage(page);
    }
}
