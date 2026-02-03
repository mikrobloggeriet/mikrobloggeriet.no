---
description: Clojure namespace aliasing conventions
---

# Namespace Aliasing

Use the **tail of the qualified namespace** as the short name unless there are conflicts.

## Examples

```clojure
;; Good
[mblog.fjernkohortene :as fjernkohortene]
[mblog.cohort :as cohort]
[babashka.fs :as fs]

;; Avoid cute abbreviations
[mblog.fjernkohortene :as fk]  ; don't do this
```

## When to deviate

Only use shorter aliases when:
1. The tail conflicts with another namespace in the same file
2. The namespace is from a well-known library with established conventions (e.g., `[clojure.string :as str]`)
