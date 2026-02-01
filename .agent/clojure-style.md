# Clojure Code Style

## REPL-Friendly Code

Prefer code that can be evaluated directly from the REPL.

### Minimize let bindings

Avoid unnecessary let bindings. The more let bindings, the less REPL-friendly the code becomes.

Use let bindings when you need to refer to the same value multiple times. Otherwise, inline the expression.

**Prefer:**
```clojure
(deftest ring-handler-returns-olorm-1
  (is (= 200
         (:status ((serve/create-ring-handler)
                   {:uri "/doc/olorm-1"
                    :request-method :get
                    :system/datomic (testdb/get-instance)})))))
```

**Avoid:**
```clojure
(deftest ring-handler-returns-olorm-1
  (let [db (testdb/get-instance)
        ring-handler (serve/create-ring-handler)
        req {:uri "/doc/olorm-1"
             :request-method :get
             :system/datomic db}
        response (ring-handler req)]
    (is (= 200 (:status response)))))
```

Referring directly to functions like `testdb/get-instance` inline lets us copy that expression and evaluate it from a REPL without needing to first evaluate the surrounding let bindings.

## Narrow Code

Write narrow code. Solve the problem at hand, don't include code that isn't needed.

- If a test assertion is clear, don't add a message to the `is` form
- Don't add tests that duplicate coverage from elsewhere
- Don't add "defensive" or "just in case" code
