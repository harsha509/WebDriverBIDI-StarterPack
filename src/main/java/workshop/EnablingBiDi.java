package workshop;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.NavigationResult;
import org.openqa.selenium.bidi.browsingcontext.ReadinessState;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnablingBiDi {
    private static final Logger logger = LoggerFactory.getLogger(EnablingBiDi.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        BrowsingContext context = null;

        try {
            // Step 1: Define browser capabilities
            ChromeOptions options = new ChromeOptions();

            // TODO: Enable WebSocket URL so Selenium can bridge to BiDi

            // Optional: any other flags you use regularly can go here
             options.addArguments("--enable-automation");
             options.addArguments("--debug-bidi-mapper");

            logger.info("Starting Chrome...");
            driver = new ChromeDriver(options);

            Capabilities caps = ((HasCapabilities) driver).getCapabilities();
            logger.info("Caps "+ caps);
            logger.info("WebDriver session started");

            // Step 2: Verify that the driver exposes the BiDi bridge
            try {
                ((HasBiDi) driver).getBiDi();
                logger.info("✅ BiDi is enabled and working: WebSocket connection established");
            } catch (Exception e) {
                logger.error("❌ BiDi is NOT enabled: Failed to establish BiDi session - {}", e.getMessage());
                return;
            }

            // Step 3: Create a BiDi BrowsingContext
            // TODO: Create a BiDi BrowsingContext from the current window

            // Step 4: Perform a small BiDi action
            String url = "data:text/html,<title>BiDi Enabled</title><h1>WebDriver BiDi is ON</h1>";
            logger.info("Navigating via BiDi to: {}", url);

            // TODO: Navigate via BiDi with readiness state COMPLETE

            Thread.sleep(30000);

        } catch (Exception e) {
            logger.error("Error during BiDi workshop step", e);
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                    logger.info("WebDriver session closed");
                } catch (Exception e) {
                    logger.error("Error closing WebDriver", e);
                }
            }
        }
    }
}
