# Workshop Step: Geolocation Emulation with WebDriver BiDi (Java)

Goal:
- Enable WebDriver BiDi on Chrome.
- Create a BiDi `BrowsingContext` for the current window.
- Apply a geolocation override via the BiDi Emulation module (e.g., San Francisco).
- Navigate to a page that displays the current geolocation to verify the override.

Prerequisites:
- Java 17+
- Maven 3.6+
- Chrome (recent stable)
- Build once: `mvn -q clean install`

Files:
- Source: `workshop/GeolocationEmulation.java` (package `workshop`)

How to run:
```bash
mvn -q clean compile exec:java -Dexec.mainClass="workshop.GeolocationEmulation"
```

Notes:
- The Java source already:
  - Enables BiDi: `options.setCapability("webSocketUrl", true)`
  - Auto-allows geolocation via Chrome prefs (no prompt)
  - Creates a `BrowsingContext` (`ctx`) from the current window
  - Opens https://browserleaks.com/geo for visual verification
- Optional headless: add `options.addArguments("--headless=new")` if needed.

## Replace the TODO in `GeolocationEmulation.java`

Apply the BiDi geolocation override (place this in the TODO under the `BrowsingContext ctx` line):

```java
GeolocationCoordinates coords = new GeolocationCoordinates(37.7749, -122.4194);
new Emulation(driver).setGeolocationOverride(
        new SetGeolocationOverrideParameters(coords).contexts(List.of(ctx.getId()))
);
```

What to observe:
- The page at https://browserleaks.com/geo should display coordinates near:
  - latitude ≈ 37.7749, longitude ≈ -122.4194

Troubleshooting:
- If permission is blocked:
  - Ensure the Chrome prefs are set (already present in the source):
    - `profile.default_content_setting_values.geolocation = 1`
  - Retry with a fresh session.
- Corporate/proxy restrictions:
  - BiDi requires a WebSocket bridge; restrictive policies may interfere.
