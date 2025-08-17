# Workshop Step: Reactive Form Validation via Console Events (Java)

Goal:
- Enable WebDriver BiDi and capture console events with LogInspector.
- Drive a self-contained reactive form and assert Submit becomes enabled.
- Count validation/form console events.

Prerequisites:
- Java 17+
- Maven 3.6+
- Chrome (recent stable)
- Build once: `mvn -q clean install`

Files:
- Source: `workshop/ReactiveFormValidation.java` (package `workshop`)

How to run:
```bash
mvn -q clean compile exec:java -Dexec.mainClass="workshop.ReactiveFormValidation"
```

## Replace the TODOs in `ReactiveFormValidation.java`

1) Enable BiDi bridge on ChromeOptions  
Replace the TODO with:
```java
options.setCapability("webSocketUrl", true); // enable BiDi bridge
```

2) Subscribe to console entries and count form-related events  
Replace the TODO block with:
```java
logs.onConsoleEntry(entry -> {
    String text = entry.getText() == null ? "" : entry.getText();
    System.out.println("Console [" + entry.getLevel() + "] " + text);
    String t = text.toUpperCase(Locale.ROOT);
    if (t.startsWith("VALIDATION_") || t.startsWith("FORM_")) formEvents.incrementAndGet();
});
```

3) Assert form is ready before submit  
Replace the TODO with:
```java
if (!submit.isEnabled()) throw new RuntimeException("Form not ready when expected");
```

What to observe:
- Validation transitions in console (VALIDATION_ERR/OK for email, pass, confirm)
- `FORM_READY` when all validations pass
- `FORM_SUBMITTED` after clicking submit
- Summary count printed: `Form-related console events captured: <n>`

Notes:
- Runs a base64 `data:` URL with inline HTML/JS; no local server needed.
- For CI/headless, add: `options.addArguments("--headless=new")`.
