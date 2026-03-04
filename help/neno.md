# Starte ting

1. I terminal, `garden run`
2. `>Calva: Connect to a running REPL server` fra Cursor
3. Velg deps.edn
4. Skriv kode
5. option + enter for å evaluere kode
    - indigo.clj må evalueres før endringer vises lokalt

# Dytte kode

1. Gjør endringer
2. Lag Git-commit
3. I terminal, `bb deploy`

# Evaluere kode

Option+Enter: evaluer topnivå-uttrykk
Ctrl+Enter: evaluer uttrykk under cursor

# Automatisk teste når noe endres

I terminalen:

    rg --files | entr -rc bb nvk-test

Kjeft på LLMen hvis det blir feil.