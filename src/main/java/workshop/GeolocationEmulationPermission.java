package workshop;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.emulation.Emulation;
import org.openqa.selenium.bidi.emulation.GeolocationCoordinates;
import org.openqa.selenium.bidi.emulation.SetGeolocationOverrideParameters;
import org.openqa.selenium.bidi.module.Permission;
import org.openqa.selenium.bidi.permissions.PermissionState;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class GeolocationEmulationPermission {
    public static void main(String[] args) throws Exception {
        ChromeOptions options = new ChromeOptions();
        // Enable BiDi
        options.setCapability("webSocketUrl", true);

        WebDriver driver = new ChromeDriver(options);

        try {
            // Create a BiDi BrowsingContext for the current window
            BrowsingContext ctx = new BrowsingContext(driver, driver.getWindowHandle());

            // Navigate first so we can grab the exact origin we’re on (must be secure)
            driver.get("https://browserleaks.com/geo");

            // Get the page origin to scope the permission
            String origin = (String) ((JavascriptExecutor) driver)
                    .executeScript("return window.location.origin;");

            // Grant geolocation permission via BiDi Permission module
            Permission permission = new Permission(driver);
            permission.setPermission(
                    Map.of("name", "geolocation"),
                    PermissionState.GRANTED,
                    origin,
                    null
            );

            // Apply BiDi geolocation override (example: San Francisco)
            GeolocationCoordinates coords = new GeolocationCoordinates(37.7749, -122.4194);
            new Emulation(driver).setGeolocationOverride(
                    new SetGeolocationOverrideParameters(coords).contexts(List.of(ctx.getId()))
            );

            // Give you a moment to see the numbers on the page
            Thread.sleep(50000);
        } finally {
            driver.quit();
        }
    }
}
