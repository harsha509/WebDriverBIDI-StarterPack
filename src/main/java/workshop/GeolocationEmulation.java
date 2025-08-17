package workshop;

/**
 * Geolocation Emulation with WebDriver BiDi (Java)
 *
 * What this demonstrates:
 *  - Enabling BiDi via ChromeOptions (webSocketUrl = true)
 *  - Creating a BiDi BrowsingContext for the current window
 *  - Granting geolocation permission via the BiDi Permission module
 *  - Overriding geolocation via the BiDi Emulation module
 *  - Reading navigator.geolocation from the page and logging the result
 *
 * How to run:
 *   mvn -q clean compile exec:java -Dexec.mainClass="workshop.GeolocationEmulation"
 *
 * Notes:
 *  - Requires a secure origin for Geolocation API (https://example.com/ is used).
 *  - You can add headless mode if needed: options.addArguments("--headless=new");
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.emulation.Emulation;
import org.openqa.selenium.bidi.emulation.GeolocationCoordinates;
import org.openqa.selenium.bidi.emulation.SetGeolocationOverrideParameters;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class GeolocationEmulation {
    public static void main(String[] args) throws Exception {
        ChromeOptions options = new ChromeOptions();

        // 1) Enable BiDi and auto-allow geolocation (no prompt)
        options.setCapability("webSocketUrl", true);
        Map<String, Object> prefs = new HashMap<>();
        // 1 = allow, 2 = block (Chrome content settings)
        prefs.put("profile.default_content_setting_values.geolocation", 1);
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);

        try {
            // 2) Apply BiDi geolocation override (example: San Francisco)
            BrowsingContext ctx = new BrowsingContext(driver, driver.getWindowHandle());
            // TODO: Apply BiDi geolocation override (example: San Francisco)
            // Steps:
            //  - Create GeolocationCoordinates with lat/lon (e.g., 37.7749, -122.4194)
            //  - Call:
            //    new Emulation(driver).setGeolocationOverride(
            //        new SetGeolocationOverrideParameters(coords).contexts(List.of(ctx.getId()))
            //    );

            // 3) Visual proof: BrowserLeaks shows what the page reads
            driver.get("https://browserleaks.com/geo");

            Thread.sleep(5000); // give you time to see the numbers
        } finally {
            driver.quit();
        }
    }
}
