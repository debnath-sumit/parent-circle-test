# parent-circle-test

Playwright + JUnit 5 end-to-end tests in Java.

## Prerequisites

- Java 17+
- Maven 3.9+

## First-time setup

```bash
mvn clean install -DskipTests
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

The second command downloads the Playwright browser binaries.

## Run tests

```bash
mvn test                          # all tests, headless chromium
mvn test -Pheaded                 # show the browser
mvn test -Dbrowser=firefox        # firefox / webkit / chromium
mvn test -Dtest=HomeTests         # single class
mvn test -Dtest=HomeTests#hasTitle # single method
```

## Config

Override via `-D` system properties or edit `src/test/resources/config.properties`:

| Property  | Default                  |
|-----------|--------------------------|
| baseUrl   | https://playwright.dev   |
| browser   | chromium                 |
| headless  | true                     |

## Debug a failure

Every test run now writes a Playwright trace zip to `target/traces/<testname>.zip`.
Open it with:

```bash
mvn dependency:copy -Dartifact=com.microsoft.playwright:playwright:1.49.0 -DoutputDirectory=target/lib
java -cp "target/lib/*" com.microsoft.playwright.CLI show-trace target/traces/signInAndSignOut.zip
```

(Or simpler if you have node: `npx playwright show-trace target/traces/signInAndSignOut.zip`.)

The trace UI shows the page DOM, screenshots, console, and network at every step.

## Layout

- `src/main/java/com/parentcircle/pages` — Page Objects
- `src/main/java/com/parentcircle/utils` — Config + Playwright factory
- `src/test/java/com/parentcircle/base` — `BaseTest` lifecycle
- `src/test/java/com/parentcircle/tests` — Test classes
- `src/test/resources` — config + test data
