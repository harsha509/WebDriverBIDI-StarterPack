package workshop;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.NavigationResult;
import org.openqa.selenium.bidi.browsingcontext.ReadinessState;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Scaffold: Console events with WebDriver BiDi (Java)
 * Live-demo friendly; includes TODOs to enable LogInspector and fetch args.
 *
 * What this demonstrates:
 *  - Enabling BiDi via ChromeOptions (webSocketUrl = true)
 *  - Creating a BiDi BrowsingContext
 *  - Subscribing to console entry events through LogInspector
 *  - Navigating to a demo page and triggering console events
 *
 * How to run (example):
 *  mvn -q clean compile exec:java -Dexec.mainClass="workshop.ConsoleEventsSubscription"
 *
 * Notes:
 *  - Requires Selenium 4.12+ (this project uses ${selenium.version} in pom).
 *  - If running in CI/headless environments, add: options.addArguments("--headless=new")
 */

public class ConsoleEventsSubscription {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleEventsSubscription.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        LogInspector logInspector = null; // <-- explicit variable as requested
        BrowsingContext context = null;

        try {
            // 1) Enable BiDi bridge
            ChromeOptions options = new ChromeOptions();
            options.setCapability("webSocketUrl", true);
            options.addArguments("--enable-automation");

            logger.info("Starting Chrome with BiDi enabled...");
            driver = new ChromeDriver(options);
            logger.info("WebDriver session established");

            // 2) Create a BiDi browsing context from current window
            context = new BrowsingContext(driver, driver.getWindowHandle());
            logger.info("BrowsingContext created. Context ID: {}", context.getId());

            // TODO: Create a LogInspector and subscribe to console entries BEFORE navigation/clicks.
            // Steps:
            //  - Instantiate LogInspector with the current driver.
            //  - Create a CompletableFuture<ConsoleLogEntry> to capture the first event.
            //  - Call logInspector.onConsoleEntry(...) and complete the future on the first entry.

            // 4) Navigate via BiDi
            String url = "https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html";
            logger.info("Navigating via BiDi to: {}", url);
            NavigationResult nav = context.navigate(url, ReadinessState.COMPLETE);
            logger.info("Navigation done. URL reported: {}, navigationId: {}", nav.getUrl(), nav.getNavigationId());

            // 5) Trigger console.error (or use #consoleLog/#consoleWarn, etc.)
            WebElement btnError = driver.findElement(By.id("consoleError"));
            logger.info("Trigger: console.error (via button click)");
            btnError.click();

            // TODO: Wait up to 5s for the async console event to arrive, then:
            //  - Log: "First console entry received: type={}, method={}, text={}" with fields from the event
            //  - Log a completion message like: "Console events subscription demo complete"

        } catch (TimeoutException te) {
            logger.error("Timed out waiting for a console entry (no event received within limit)", te);
        } catch (Exception e) {
            logger.error("Error during console events subscription example", e);
        } finally {
            if (logInspector != null) {
                try {
                    logInspector.close();
                    logger.info("LogInspector closed");
                } catch (Exception e) {
                    logger.error("Error closing LogInspector", e);
                }
            }
            if (driver != null) {
                try {
                    driver.quit();
                    logger.info("WebDriver session closed");
                } catch (Exception e) {
                    logger.error("Error closing WebDriver session", e);
                }
            }
        }
    }
}
