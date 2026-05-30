package com.parentcircle.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.parentcircle.utils.ConfigReader;
import com.parentcircle.utils.PlaywrightFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseTest {

    private static final Path TRACE_DIR = Paths.get("target/traces");
    private static final Path SCREENSHOT_DIR = Paths.get("target/screenshots");

    /**
     * Saves a full-page PNG to target/screenshots/&lt;method&gt;.png ONLY when a test fails.
     * Registered as an instance extension because AfterTestExecutionCallback fires right
     * after the test body but BEFORE tearDown() closes the page — an @AfterEach or
     * TestWatcher would run too late, after the page is already closed.
     */
    @RegisterExtension
    final AfterTestExecutionCallback screenshotOnFailure = ctx -> {
        if (ctx.getExecutionException().isPresent() && this.page != null) {
            Files.createDirectories(SCREENSHOT_DIR);
            String name = ctx.getRequiredTestMethod().getName();
            this.page.screenshot(new Page.ScreenshotOptions()
                    .setPath(SCREENSHOT_DIR.resolve(name + ".png"))
                    .setFullPage(true));
        }
    };

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(TRACE_DIR);
        baseUrl = ConfigReader.get("base.url", "http://localhost:3001");

        playwright = Playwright.create();
        browser = PlaywrightFactory.launch(playwright);
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @AfterEach
    void tearDown(TestInfo info) {
        String name = info.getTestMethod().map(Method::getName).orElse("test");
        if (context != null) {
            try {
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(TRACE_DIR.resolve(name + ".zip")));
            } catch (Exception ignored) {
            }
            context.close();
        }
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
