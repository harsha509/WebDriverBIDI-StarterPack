# Workshop Step 1: Enabling BiDi in browser capabilities (Java)

Goal:
- Start a WebDriver session with WebDriver BiDi enabled.
- Verify BiDi bridge availability in Selenium.
- Perform a trivial BiDi operation (navigate) to prove it works.

Prerequisites:
- Java 17+
- Maven 3.6+
- Chrome (recent stable) installed
- Internet access for dependency resolution (Selenium Manager can auto-provision chromedriver)
- This repo built at least once: `mvn -q clean install`

Files:
- Source: `workshop/EnablingBiDi.java` (package `workshop`)

How to run:
- Using Maven Exec:
  ```bash
  mvn -q exec:java -Dexec.mainClass="workshop.EnablingBiDi"
  ```

## Replace the TODOs in `EnablingBiDi.java`

1) **Enable BiDi bridge on ChromeOptions**  
   Replace the TODO with:
```java
options.setCapability("webSocketUrl", true);
```

2) **Create a BiDi BrowsingContext from the current window**  
   Replace the TODO with:
```java
context = new BrowsingContext(driver, driver.getWindowHandle());
logger.info("BrowsingContext created. Context ID: {}", context.getId());
```

3) **Navigate via BiDi (wait for COMPLETE) and log result**  
   Replace the TODO with:
```java
NavigationResult nav = context.navigate(url, ReadinessState.COMPLETE);
logger.info("Navigation done. URL reported: {}, navigationId: {}", nav.getUrl(), nav.getNavigationId());
```

What to observe in logs:
- Detection of BiDi bridge:
    - `✅ HasBiDi detected: Selenium BiDi bridge is available`
- BrowsingContext created:
    - `BrowsingContext created. Context ID: <id>`
- BiDi navigation completed:
    - `Navigation done. URL reported: data:text/html,... navigationId: <navId>`

Expected outcome:
- Program finishes and the browser closes.

Common pitfalls:
- Old Chrome: Some BiDi features may be missing. Update to a recent stable Chrome.
- Corporate/proxy restrictions: BiDi relies on a WebSocket bridge; blocked connections can break the session.
- Headless policies: If your environment forces headless, add `options.addArguments("--headless=new")` and re-run.

Exercises:
1) Switch to headless mode:
    - Add `options.addArguments("--headless=new")` in `EnablingBiDi.java`.
    - Re-run and confirm logs are identical.
2) Change the test URL to an HTTPS page (e.g., https://example.com) and observe the navigation result.

Teardown:
- The example calls `driver.quit()` in `finally`, which shuts down all contexts and the ChromeDriver process.
