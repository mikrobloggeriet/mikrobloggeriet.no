Always use invoker (`nvk` or `bb nvk`) to interact with the running clojure process and when running clojure functions/syncing deps.
Do not use `clojure` directly for these unless necessary.

Pages should return Hiccup data structures, not raw HTML strings. Read pages from the page registry when needed.

---

**Application.Garden CLI (`garden`) rules:**
DO NOT use remote `garden` commands. You are to use LOCAL COMMANDS ONLY for local development.

- **Local Commands (Safe to run):**
  - `garden run` - Run a project locally. Use this to start the local dev server.
  - `garden init` - Initialize a project.
  - `garden help` / `garden version`

- **Remote Commands (DANGEROUS! Do NOT run locally!):**
  - `garden restart` - Restarts the DEPLOYED production instance.
  - `garden deploy`, `garden delete`, `garden stop`, `garden publish`, `garden repl`, `garden sftp`, `garden secrets`, etc.
