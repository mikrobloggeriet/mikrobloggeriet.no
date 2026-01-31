---
description: How to run the test suite
---

# Running Tests

This project uses [Kaocha](https://github.com/lambdaisland/kaocha) as the test runner.

## Run all tests

```bash
bb test
```

This runs Kaocha via the Babashka task defined in `bb.edn`.

## After writing code

// turbo
Always run the tests after writing or modifying code:

```bash
bb test
```

Ensure all tests pass (exit code 0) before considering a task complete.
