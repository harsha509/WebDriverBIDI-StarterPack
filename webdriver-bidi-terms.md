# WebDriver BiDi — Key Terms (Simple Words)

> This sheet summarizes terminology from the W3C **WebDriver BiDi** specification, using only that document: https://www.w3.org/TR/webdriver-bidi/

## What is WebDriver BiDi?
WebDriver BiDi adds **bidirectional** communication between your client (test code) and the browser: you can send **commands** to the browser, and the browser can send **events** back over the same channel.

---

## Ends (who talks to whom)
- **Local end**: the controlling side (your client).
- **Remote end**: the browser side (the implementation).

---

## Transport & connection
- Messages travel over **WebSocket**.
- When you request a new WebDriver session with `webSocketUrl: true`, the browser returns a **WebSocket URL** that you use to open the BiDi connection for that session.

---

## Sessions
- A regular WebDriver **session** becomes a **BiDi session** when its **BiDi flag** is set (i.e., when the WebSocket URL is provided).

---

## Modules, commands, events, subscriptions
- The protocol is split into **modules** (for example: `browsingContext`, `script`, `browser`, `network`, etc.).
- A **command** is an asynchronous request from the local end; it has a **`method`** (name) and **`params`** (data). Results and errors are matched by a command **id** and may arrive **out of order**.
- An **event** is a notification from the remote end; it also has a **`method`** (event name) and **`params`** (data).
- A session tracks **subscriptions** so you receive only the events you asked for.

---

## Browsing Context (module: `browsingContext`)

- In WebDriver BiDi, `browsingContext` is the module and ID you use to point at a specific “page or frame.”
- **`browsingContext` (id)**: a string identifier for a browsing context. In the spec, this maps to the HTML concept of a **navigable** (top‑level like a tab/window, or a child like an `<iframe>`).
- **`context` (parameter)**: wherever you see `context` in commands/events, it refers to that **browsing context id**.
- **`browsingContext.Info`**: the structure returned by APIs like `getTree`, describing a navigable. It can include:
  - `context` (the id), optional `parent`, `children` (child contexts), `url` (current), `userContext`, `originalOpener`, and `clientWindow`.
- **Activate**: focuses the given **top‑level** context.
- **Locators**: strategies used by `browsingContext.locateNodes` to find nodes: **`css`**, **`xpath`**, **`innerText`**, **`accessibility`**, and **`context`**.
- **Navigation (id) & info**: each navigation gets a text **`navigation`** id; navigation info includes `context`, `navigation` (or `null`), `timestamp`, and `url`.
- **`ReadinessState`**: when navigation‑related commands resolve: `"none"`, `"interactive"`, or `"complete"`.
- **User prompts**:
  - **`UserPromptType`**: `"alert"`, `"beforeunload"`, `"confirm"`, `"prompt"`.
  - Related events: `userPromptOpened` and `userPromptClosed`.

---

## Browser (module: `browser`)
- **`ClientWindow` (id)**: identifies a browser **client window** (an OS‑level window). `ClientWindowInfo` reports properties such as state, size, position, and whether it’s active.
- Commands allow you to list and query client windows.

---

## User Contexts (spec section on user contexts)
- A **user context** is a group of zero or more **top‑level** browsing contexts (tabs/windows) that share a **storage partition** separate from other user contexts.
- There is a **default** user context with id **`"default"`**.
- A **child** browsing context uses the **same user context** as its parent.

---

## Script (module: `script`)
- **Realm (id)**: each JavaScript **realm** has a string id. Script APIs expose and target these realms.
- **Target**: where to run your script. It can be:
  - a **realm**: `{ realm }`
  - a **browsing context** (optionally with a **sandbox** name): `{ context, sandbox? }`
- If you target a **context**, the spec defines how this maps to the active document’s realm (or to a sandboxed realm when `sandbox` is given).

---

## Sandboxed script execution
- The session maintains a **sandbox map** so scripts can run in **sandbox realms** that have access to the document’s DOM but are isolated from any page‑level changes to the JavaScript environment.

---

## Errors
- The spec defines protocol‑level **error codes** for BiDi (for example, errors referring to user contexts or client windows). These extend the WebDriver error set.

---

## Message format (high level)
- Command message → local end → remote end: includes **id**, **`method`**, **`params`**.
- Result/Error message → remote end → local end: matched to the **id**.
- Event message → remote end → local end: includes **`method`** (event name) and **`params`**.

---

## Quick mapping tips (within the spec’s terms)
- When an API asks for **`context`**, pass the **browsingContext id** you got from session start or `browsingContext.getTree`.
- For script execution, choose a **Target**: a **realm** or a **context** (with optional `sandbox`).

---

*Source: W3C WebDriver BiDi specification — https://www.w3.org/TR/webdriver-bidi/*
