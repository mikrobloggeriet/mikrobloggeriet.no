---
description: How to use nvk (invoker) for REPL interaction and code reload
---

# nvk Development Workflow

Invoker (`nvk`) provides CLI access to a running Clojure nREPL.

## Prerequisites

- A running REPL with `.nrepl-port` file present
- nvk installed via `bbin install io.github.filipesilva/invoker`

## Invoke a function

```bash
nvk namespace/function arg1 arg2
```

Example:
```bash
nvk clojure.core/+ 1 2 3
# => 6
```

## Reload changed namespaces

// turbo
```bash
nvk reload
```

This uses clj-reload to reload any namespaces with changed source files.

## Run tests

// turbo
```bash
nvk test
```

Run all tests in `test/**/*.clj`, reloading changed files first.

Target a specific namespace:
```bash
nvk test mblog.system-test
```

Target a specific test:
```bash
nvk test mblog.system-test/some-test-name
```

## Development Flow

1. User starts the app and connects via nREPL
2. Agent makes code changes
3. Agent reloads with `nvk reload`
4. Agent verifies via `nvk test` or by invoking functions with `nvk`
