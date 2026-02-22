---
description: How to run the test suite
---

# Running Tests

This project uses [Kaocha](https://github.com/lambdaisland/kaocha) as the test runner.

## IMPORTANT AGENT RULE: Avoid Process Restarts

NEVER run `bb test`. Running `bb test` starts a completely new Clojure process which is slow.

Instead, ALWAYS use `bb nvk test` after writing or modifying code. This command uses `nvk` to trigger a re-run of the full test suite in the already-running nREPL process.

## After writing code

// turbo
```bash
bb nvk test
```

This uses `bb nvk` to automatically reload changed files and run the full test suite quickly inside the existing REPL. Ensure all tests pass (exit code 0) before considering a task complete.
