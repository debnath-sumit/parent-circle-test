# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Playwright + JUnit 5 end-to-end UI test suite in Java 17, built with Maven. Targets a web app at `base.url` (default `http://localhost:3001`).

## Commands

```bash
# First-time setup — install deps, then download Playwright browser binaries
mvn clean install -DskipTests
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Run tests
mvn test                            # all tests, headless chromium
mvn test -Pheaded                   # show the browser (headed profile)
mvn test -Dbrowser=firefox          # firefox | webkit | chromium
mvn test -Dtest=HomeTests           # single class
mvn test -Dtest=HomeTests#hasTitle  # single method

# Override config at runtime
mvn test -Dbase.url=... -Dtest.email=... -Dtest.password=...
```

Tests run in **parallel by class, 2 threads** (configured in `maven-surefire-plugin`). Each test class is independent.

## Configuration & precedence

`ConfigReader` (`utils/ConfigReader.java`) is the single config gateway. Resolution order for any key:

1. System property (`-Dkey=...`, e.g. from Maven or the Jenkins pipeline)
2. `src/test/resources/config.properties`
3. Hard-coded fallback passed to `get(key, default)`

Keys: `base.url`, `browser`, `headless`, `test.email`, `test.password`.

The `headed` Maven profile sets `headless=false` via `argLine`. Note: Maven profile/surefire system properties **override `config.properties` silently** — if a config value seems ignored, check for a `-D` or profile default before assuming the file is wrong.

## Architecture

**Page Object Model with a strict actions/locators split.** This is the core convention — follow it for any new page:

- `pages/<area>/<Name>Page.java` — **actions only**. Methods like `signIn(user)`, `goToSignIn()`. Extends `BasePage` (which holds the `Page` and `title()`/`url()` helpers). Holds no selectors.
- `pages/<area>/<Name>PageLocators.java` — **locators only**, no clicks/fills. When the UI changes, this is the *only* file to edit. Prefer role-based selectors (`getByRole(AriaRole.TEXTBOX, ...setName(...))`).
- A page composes its locators class and exposes it via `.locators()`; tests assert against `page.locators().emailInput()`.

**Shared UI fragments** live in `pages/components/` (e.g. `NavigationComponent`) and are composed into pages — `HomePage` holds a `NavigationComponent` rather than redefining nav locators.

**Test lifecycle** is centralized in `BaseTest` (`src/test/java/com/parentcircle/base/`). All test classes extend it. Per-test `@BeforeEach` creates a fresh `Playwright` → `Browser` (via `PlaywrightFactory`, which reads `browser`/`headless`) → `BrowserContext` → `Page`, and starts a trace. `@AfterEach` writes a trace zip to `target/traces/<testMethodName>.zip` and tears everything down. Tests get `page` and `baseUrl` as protected fields.

**Credentials / test users**: never hard-code in test classes. `TestUsers.standard()` builds a `TestUser` record from `test.email`/`test.password` via `ConfigReader`, throwing if unset. `testdata/users.json` holds additional fixture users.

## Debugging a failed test

Every run writes a Playwright trace. Open it:

```bash
npx playwright show-trace target/traces/<testname>.zip   # if node available
```

The trace shows DOM, screenshots, console, and network at each step. Before diagnosing a failure, inspect the trace screenshot — what the browser actually loaded often differs from the assumed `base.url`.

On **failure only**, `BaseTest` also saves a standalone full-page PNG to `target/screenshots/<testMethodName>.png` (via an `AfterTestExecutionCallback` — it must run before `tearDown()` closes the page, so don't reimplement this with a `TestWatcher`/`@AfterEach`).

## Notes

- `src/main/java/practices/java/` (e.g. `ReverseString`, `PalindromeString`) is unrelated Java-exercise scratch code, **not** part of the test framework.
- The config table in `README.md` lists a stale `baseUrl` default (`https://playwright.dev`); the actual default is `http://localhost:3001` (see `config.properties` / `BaseTest`).
