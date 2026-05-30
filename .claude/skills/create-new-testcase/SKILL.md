---
name: create-new-testcase
description: Use when adding a new end-to-end test, test class, page object, or user-flow automation to this Playwright/JUnit repo — covering a new screen, feature, or interaction.
---

# Create a New Test Case

Tests here are Playwright + JUnit 5 using a **Page Object Model with a strict actions/locators split**. A new test almost always means writing (or reusing) a Page + its Locators, then a thin test that orchestrates them. Follow the existing pattern exactly — do not put selectors or `click()`/`fill()` in test classes.

## Where things live

| What | Path | Role |
|---|---|---|
| Test class | `src/test/java/com/parentcircle/tests/<Name>Tests.java` | Orchestration + assertions only |
| Page actions | `src/main/java/com/parentcircle/pages/<area>/<Name>Page.java` | Methods like `signIn(user)`. Extends `BasePage`. **No selectors.** |
| Page locators | `src/main/java/com/parentcircle/pages/<area>/<Name>PageLocators.java` | `Locator` getters only. **No clicks/fills.** |
| Shared fragment | `src/main/java/com/parentcircle/pages/components/<X>/` | Reused across pages (e.g. navigation) |

## Recipe

1. **Reuse or create the Page.** If the screen already has a `*Page`, add a method to it. Otherwise create `<Name>PageLocators` (role-based selectors) **and** `<Name>Page` (actions that call the locators). Compose shared UI via existing components — don't redefine nav/header locators.
   - **Navigation between pages:** add a `goTo<Screen>()` action on the *originating* page (or component) that performs the click and **returns the next page object** — exactly like `HomePage.goToSignIn()` returns a `SignInPage`. Don't `new SomePage(page)` cold in the test when a real nav action exists.
2. **Write the test class** in `tests/`, `extends BaseTest`. Use the inherited `page` and `baseUrl` fields. Start from `new HomePage(page).open(baseUrl)` and navigate via page methods.
3. **Assert with web-first assertions**: `import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;` then `assertThat(locator).isVisible()`. These auto-wait — never add manual sleeps.
4. **Credentials** come from `TestUsers.standard()` (reads `test.email`/`test.password` via `ConfigReader`). **Never hard-code credentials** in a test or page.
5. **Annotate** each `@Test` with a `@DisplayName` describing the user-visible behavior.
6. **Verify** the single test: `mvn test -Dtest=<Name>Tests#<method>` (app must be reachable at `base.url`).

## Example — adding a "Profile" screen test

```java
// pages/profile/ProfilePageLocators.java — selectors ONLY
public class ProfilePageLocators {
    private final Page page;
    public ProfilePageLocators(Page page) { this.page = page; }
    public Locator heading()   { return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile")); }
    public Locator emailField(){ return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")); }
}

// pages/profile/ProfilePage.java — actions ONLY
public class ProfilePage extends BasePage {
    private final ProfilePageLocators loc;
    public ProfilePage(Page page) { super(page); this.loc = new ProfilePageLocators(page); }
    public ProfilePageLocators locators() { return loc; }
    public void updateEmail(String email) { loc.emailField().fill(email); }
}

// tests/ProfileTests.java — orchestration + assertions ONLY
class ProfileTests extends BaseTest {
    @Test
    @DisplayName("signed-in user sees their profile heading")
    void profileVisibleAfterSignIn() {
        HomePage home = new HomePage(page).open(baseUrl);
        home.goToSignIn().signIn(TestUsers.standard());
        ProfilePage profile = home.goToProfile();   // nav action returns the next page object (add it on HomePage)
        assertThat(profile.locators().heading()).isVisible();
    }
}
```

## Locator preference (best → avoid)

`getByRole(...).setName(...)` → `getByLabel` / `getByPlaceholder` / `getByText` → `getByTestId` → CSS/XPath (last resort). Match the role-based style already in `SignInPageLocators`.

## Common mistakes

| Mistake | Fix |
|---|---|
| `click()`/`fill()` inside a test class | Move it into the `*Page`; tests only orchestrate + assert |
| Selectors inside the `*Page` | Selectors live only in `*PageLocators` |
| Hard-coded email/password | Use `TestUsers.standard()` |
| `Thread.sleep` / manual waits | Use `PlaywrightAssertions.assertThat(locator)` — it auto-waits |
| New Playwright/Browser in the test | `BaseTest` already provides `page`; just use it |
| Forgetting `@DisplayName` | Add one describing the behavior |

## Verify before done

```bash
mvn test -Dtest=<Name>Tests#<method>
```
On failure, read `target/surefire-reports/`, the trace `target/traces/<method>.zip`, and the failure PNG `target/screenshots/<method>.png`. See the `debugging-failing-tests` skill.
