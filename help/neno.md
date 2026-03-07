# Starte ting

I terminal 1:

1. `garden run`
2. `>Calva: Connect to a running REPL server` fra Cursor
3. Velg deps.edn
4. Skriv kode

I terminal 2:

1. `rg --files | entr -rc bb nvk-test`

Putt terminal 2 i sidesynet, den sier ifra når du brekker ting.

LLM-en din kan hjelpe deg å diagnostisere.

# Evaluere kode

Option+Enter: evaluer topnivå-uttrykk
Ctrl+Enter: evaluer uttrykk under cursor

# Publisere Mikrobloggeriet

1. Gjør endringer
2. Lag Git-commit
3. I terminal, `bb deploy`
