---
name: debugging-failing-tests
description: Use when a Playwright/JUnit test in this repo fails, times out on a locator assertion, "hits the wrong site", loads a blank/wrong page, behaves intermittently, or a -D/base.url override seems ignored.
---

# Debugging Failing Tests

End-to-end tests here are Playwright + JUnit 5 (Java/Maven). Failures are almost always **environment or config**, not the test logic. Read the artifacts before reading the test.

## Triage order (do this first, in order)

1. **Read the surefire report**, not the console:
   `target/surefire-reports/com.parentcircle.tests.<Class>.txt` (and the matching `TEST-*.xml`). It shows the exact failing line and exception — e.g. `net::ERR_CONNECTION_REFUSED at http://localhost:3001/` means the assertion never ran; navigation failed first.
2. **Open the trace** for that method — `BaseTest` writes one per test to `target/traces/<testMethodName>.zip`:
   ```bash
   npx playwright show-trace target/traces/<method>.zip
   ```
   A **blank white screenshot** = nothing loaded at `base.url` (app down / wrong URL), not a selector bug.
3. **Resolve the effective config** before blaming the test. `ConfigReader` precedence is: **system property (`-Dkey=...`) → `config.properties` → hard-coded default.**

## The config-precedence traps (most common root cause)

- The key is **`base.url`**, not `baseUrl`. `-DbaseUrl=...` is **silently ignored** and the run falls back to `config.properties` (`http://localhost:3001`). Stale `baseUrl` references still linger in `README.md` and a `pom.xml` comment — don't trust them.
- The `headed` Maven **profile and surefire `argLine` inject `-D` values that override `config.properties` silently.** If a `config.properties` value seems ignored, look for a profile/`-D` first.
- A bare host with no scheme (`localhost:3001` vs `http://localhost:3001`) also fails with `ERR_CONNECTION_REFUSED`.
- Confirm the app is actually up: `lsof -iTCP:3001 -sTCP:LISTEN`.

## "Intermittent / wrong page loaded" is usually a red herring

Surefire runs `<parallel>classes</parallel>` with `threadCount=2`, so two classes run at once and their logs/traces interleave. That looks like pages crossing over. Check the trace — every recorded navigation will actually go to the same `base.url`. It's concurrency in the *output*, not a real cross-navigation.

## Confirm the fix

Re-run the single method against a reachable target, headed, with the **correct** key:
```bash
mvn test -Dtest=<Class>#<method> -Dbase.url=https://real-host -Pheaded
```

## Common mistakes

| Symptom | Real cause |
|---|---|
| Locator `isVisible()` times out | Navigation already failed — check surefire report, not the locator |
| Blank screenshot in trace | App not running / wrong `base.url` |
| `-D` override "doesn't work" | Used `baseUrl` instead of `base.url` |
| `config.properties` value ignored | A profile/surefire `argLine` `-D` is overriding it |
| "Wrong page intermittently" | Parallel classes interleaving output — not a real bug |
