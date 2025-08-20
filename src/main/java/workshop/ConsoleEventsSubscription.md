# Workshop: Console Events Subscription with WebDriver BiDi (Java)

Goal:
- Subscribe to console entry events via WebDriver BiDi.
- Trigger a console event on a demo page and assert that it was received.

Prerequisites:
- Java 17+
- Maven 3.6+
- Chrome (recent stable)
- Built once: `mvn -q clean install`

Files:
- Source: `workshop/ConsoleEventsSubscription.java` (package `workshop`)

How to run:
```bash
mvn compile exec:java -Dexec.mainClass="workshop.ConsoleEventsSubscription"
```

## Replace the TODOs in `ConsoleEventsSubscription.java`

1) Create a LogInspector and subscribe BEFORE navigation/clicks  
Replace the TODO block with:
```java
logInspector = new LogInspector(driver); // <-- individual assignment
CompletableFuture<ConsoleLogEntry> consoleFuture = new CompletableFuture<>();

logInspector.onConsoleEntry(entry -> {
    // log useful details; don't miss the {} placeholders!
    logger.info("console {}:{} -> {}",
            entry.getType(), entry.getMethod(), entry.getText());
    // complete only once
    if (!consoleFuture.isDone()) {
        consoleFuture.complete(entry);
    }
});
```

2) Wait for the console event and log details  
Replace the TODO block with:
```java
// 6) Wait up to 5s for the async console event to arrive
ConsoleLogEntry e = consoleFuture.get(5, TimeUnit.SECONDS);
logger.info("First console entry received: type={}, method={}, text={}",
        e.getType(), e.getMethod(), e.getText());

logger.info("Console events subscription demo complete");
```

What to observe in logs:
- Subscription activity (lines starting with `console ...`)
- After clicking the button:
  - `First console entry received: type=..., method=..., text=...`
  - Completion message: `Console events subscription demo complete`

Notes:
- If running headless/CI, add: `options.addArguments("--headless=new")`.
- The example already enables BiDi (`webSocketUrl = true`) and creates a `BrowsingContext`; only the subscription and wait/log steps are TODOs here.
