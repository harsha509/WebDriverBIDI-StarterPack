package workshop;

/**
 * Network events with WebDriver BiDi (Java)
 *
 * What this demonstrates:
 *  - Enabling BiDi via ChromeOptions (webSocketUrl = true)
 *  - Detecting Selenium's BiDi bridge (HasBiDi) and creating the Network module
 *  - Subscribing to network events:
 *      • onBeforeRequestSent  — log outgoing requests (method + URL)
 *      • onResponseCompleted  — log completed responses (event summary)
 *      • onFetchError         — log failed requests with error text
 *  - Navigating to a page to generate network traffic
 *
 * Why this is useful:
 *  - Lets you observe/trace network activity during automated tests
 *  - Helps debug API calls, CORs issues, and failing requests
 *  - Can be extended to collect metrics or assert on network behavior
 *
 * How to run (example):
 *   mvn -q clean compile exec:java -Dexec.mainClass="workshop.NetworkEvents"
 *
 * Notes:
 *  - This project uses Selenium ${selenium.version} (see pom.xml).
 *  - For CI/headless environments, you may add: options.addArguments("--headless=new")
 */


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.ResponseDetails;
import org.openqa.selenium.bidi.network.RequestData;
import org.openqa.selenium.bidi.network.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkEvents {
    private static final Logger logger = LoggerFactory.getLogger(NetworkEvents.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.setCapability("webSocketUrl", true);

            driver = new ChromeDriver(options);
            BiDi bidi = ((HasBiDi) driver).getBiDi();
            logger.info("BiDi bridge acquired: {}", bidi != null);

            // TODO: Network Events Exercise (see workshop/NetworkEvents.md for full solution)
            // 1) Create the Network module:
            //    Network network = new Network(driver);
            //
            // 2) Subscribe to outgoing requests:
            //    - Use network.onBeforeRequestSent(...) to log method + URL.
            //
            // 3) Subscribe to completed responses:
            //    - Use network.onResponseCompleted(...) to log method, URL, status, mime type.
            //    - Iterate over response headers and log "name: value" using decodeHeaderValue(...).
            //
            // 4) Subscribe to failed requests:
            //    - Use network.onFetchError(...) to log the failing URL and error text.
            //
            // Place the above before navigation so events are captured when the page loads.

            driver.get("https://selenium.dev");
            Thread.sleep(1500);
        } catch (Exception e) {
            logger.error("Network events run failed", e);
        } finally {
            if (driver != null) {
                try { driver.quit(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Decode Selenium's header value wrapper (e.g., BytesValue) into a readable String,
     * working across Selenium versions without requiring Java 16+ features.
     */
    private static String decodeHeaderValue(Object value) {
        if (value == null) return "";
        // Try common methods that return a String
        String[] methods = { "getValue", "asString", "toStringValue" };
        for (String m : methods) {
            try {
                Object v = value.getClass().getMethod(m).invoke(value);
                if (v != null) return v.toString();
            } catch (Exception ignored) {}
        }
        // Some versions may expose Optional<String>
        try {
            Object opt = value.getClass().getMethod("getValue").invoke(value);
            if (opt instanceof java.util.Optional) {
                java.util.Optional<?> o = (java.util.Optional<?>) opt;
                if (o.isPresent()) return String.valueOf(o.get());
            }
        } catch (Exception ignored) {}

        // Some versions might store bytes; handle byte[]
        try {
            Object bytes = value.getClass().getMethod("getBytes").invoke(value);
            if (bytes instanceof byte[]) return new String((byte[]) bytes);
        } catch (Exception ignored) {}

        // Fallback (may still look like BytesValue@xxxx if nothing else worked)
        return value.toString();
    }
}
