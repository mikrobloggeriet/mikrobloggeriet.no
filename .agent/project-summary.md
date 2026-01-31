# Mikrobloggeriet Project Summary

## What is this?

**Mikrobloggeriet** is a collaborative micro-blogging platform written in Clojure. The name is Norwegian for "The Micro-blogging". It's hosted at https://mikrobloggeriet.no/

Tagline: *"Upolert nysgjerrighet"* ("Unpolished curiosity")

## Technology Stack

- **Language**: Clojure (runs on JVM)
- **Web Framework**: Reitit (routing), HTTP-Kit (server), Hiccup (HTML templating)
- **Database**: Datomic
- **Test Runner**: Kaocha
- **Build/Task Runner**: Babashka (`bb.edn`)
- **Deployment**: Application Garden (`garden` CLI)

## Architecture

- `src/` - Main application code
  - `mikrobloggeriet/` - Core web application namespaces
  - `mblog/` - CLI and blog management logic
- `test/` - Test files (mirrors src structure)
- `dev/` - Development utilities
- `text/` - Markdown content files
- `public/` - Static assets
- `theme/` - Theming resources

## Key Concepts

| Term | Description |
|------|-------------|
| **cohort** | A group of people writing together (e.g., `olorm`, `jals`, `vakt`) |
| **doc** | A single microblog entry/post |
| **mblog.sh** | CLI tool for creating new posts |

## UI Features

- Split-pane layout: sidebar with post list, main content area
- **Randomized theming**: Each page load generates a random color scheme and typography—refreshing shows a new theme
- Posts organized by cohort prefixes (e.g., `olorm-61`, `vakt-3`)

## Common Commands

```bash
# Run tests
bb test

# Start development server
garden run
# App runs at http://localhost:7777

# Deploy to production
bb deploy
```

## REPL Development

From REPL (e.g., via Calva):
```clojure
(start!)  ; Start the HTTP server
(stop!)   ; Stop the server
```

These functions are defined in `src/user.clj`.
