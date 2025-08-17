# Workshop Step: Network Events with WebDriver BiDi (Java)

Goal:
- Enable WebDriver BiDi on Chrome.
- Subscribe to network events (request, response, failures).
- Observe request/response details in logs, including headers.

Prerequisites:
- Java 17+
- Maven 3.6+
- Chrome (recent stable) installed
- Internet access for dependency resolution (Selenium Manager can auto-provision chromedriver)
- Build once to resolve dependencies: `mvn -q clean install`

Files:
- Source: `workshop/NetworkEvents.java` (package `workshop`)

How to run:
- Using Maven Exec:
  ```bash
  mvn -q clean compile exec:java -Dexec.mainClass="workshop.NetworkEvents"
  ```

Notes:
- Headless environments: add `options.addArguments("--headless=new")` in `NetworkEvents.java` if needed.
- The helper method `decodeHeaderValue` already exists in `NetworkEvents.java` to make header values readable.

---

## Implement the TODOs in `NetworkEvents.java`

Where to add:
- In `main`, after acquiring the BiDi bridge:
  ```java
  BiDi bidi = ((HasBiDi) driver).getBiDi();
  logger.info("BiDi bridge acquired: {}", bidi != null);
  // Insert your Network module + subscriptions below
  ```
- Keep the existing `decodeHeaderValue` helper as-is.

### 1) Create the Network module
```java
Network network = new Network(driver);
```

### 2) Subscribe to outgoing requests (onBeforeRequestSent)
```java
network.onBeforeRequestSent(e ->
        logger.info("[REQ] {} {}", e.getRequest().getMethod(), e.getRequest().getUrl())
);
```

### 3) Subscribe to completed responses (onResponseCompleted)
```java
network.onResponseCompleted((ResponseDetails e) -> {
    RequestData req = e.getRequest();
    ResponseData res = e.getResponseData();
    if (req != null && res != null) {
        logger.info("[RES] {} {} -> status={} mime={}",
                req.getMethod(), req.getUrl(), res.getStatus(), res.getMimeType());

        if (res.getHeaders() != null) {
            res.getHeaders().forEach(h ->
                    logger.info("   {}: {}", h.getName(), decodeHeaderValue(h.getValue()))
            );
        }
    }
});
```

### 4) Subscribe to failed requests (onFetchError)
```java
network.onFetchError(e ->
        logger.warn("[FAIL] {} error={}", e.getRequest().getUrl(), e.getErrorText())
);
```

---

## Suggested solution (complete block)

Paste this block under the BiDi acquisition log line:

```java
Network network = new Network(driver);

network.onBeforeRequestSent(e ->
        logger.info("[REQ] {} {}", e.getRequest().getMethod(), e.getRequest().getUrl())
);

network.onResponseCompleted((ResponseDetails e) -> {
    RequestData req = e.getRequest();
    ResponseData res = e.getResponseData();
    if (req != null && res != null) {
        logger.info("[RES] {} {} -> status={} mime={}",
                req.getMethod(), req.getUrl(), res.getStatus(), res.getMimeType());

        if (res.getHeaders() != null) {
            res.getHeaders().forEach(h ->
                    logger.info("   {}: {}", h.getName(), decodeHeaderValue(h.getValue()))
            );
        }
    }
});

network.onFetchError(e ->
        logger.warn("[FAIL] {} error={}", e.getRequest().getUrl(), e.getErrorText())
);
```

---

## What to observe in logs

- Outgoing requests:
  - `[REQ] GET https://...`
- Completed responses:
  - `[RES] GET https://... -> status=200 mime=text/html`
  - Headers (each line indented):
    - `   content-type: text/html; charset=UTF-8`
- Failures:
  - `[FAIL] https://... error=net::ERR_*`

Tip: Run once to warm caches, then re-run to compare the difference in requests (e.g., cached assets, fewer network calls).

---

## Troubleshooting

- No logs appear:
  - Ensure you added the Network subscriptions before `driver.get(...)`.
  - Verify BiDi is enabled: `options.setCapability("webSocketUrl", true)`.
- Headless differences:
  - Add `--headless=new` and re-run. Some sites behave differently in headless.
- Corporate proxy / network restrictions:
  - BiDi uses a WebSocket bridge. Firewall/proxy policies may affect connectivity.

---

## Exercises

1) Filter by method:
   - Modify the request subscription to only log `POST` requests.
2) Collect metrics:
   - Count the number of responses with `status >= 400` and log the total at the end.
3) Explore headers:
   - Print only `content-type` and `cache-control` headers for each response.

Teardown:
- The example already calls `driver.quit()` in `finally`, which shuts down Chrome cleanly.
