Always use invoker (`nvk` or `bb nvk`) to interact with the running clojure process and when running clojure functions/syncing deps.
Do not use `clojure` directly for these unless necessary.

Pages should return Hiccup data structures, not raw HTML strings. Read pages from the page registry when needed.
