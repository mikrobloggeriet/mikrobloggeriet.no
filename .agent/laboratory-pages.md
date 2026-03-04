## Laboratory pages (`/laboratoriet`)

- **Hva**: Laboratory pages er tidlige prototyper. De skal være _tydelige_, _direkte_ og _enkle å eksperimentere med_.
- **Hvor (URL)**: Alle laboratorie-sider lever under `/laboratoriet`, f.eks. `/laboratoriet/chat/`.
- **Hvor (filer)**: HTML og CSS ligger under `public/laboratoriet/**`, f.eks.:
  - `public/laboratoriet/chat/index.html`
  - `public/laboratoriet/chat/styles.css`

### Regler for struktur

- **En mappe per eksperiment**:
  - `public/laboratoriet/<navn>/index.html`
  - `public/laboratoriet/<navn>/styles.css`
- `index.html`:
  - Ren, statisk HTML. Ingen Clojure, ingen templating, ingen JS.
  - Refererer kun til sin egen CSS med `<link rel="stylesheet" href="./styles.css">` (eller tilsvarende relativ sti).
- `styles.css`:
  - Inneholder alle stilene som trengs for akkurat denne laboratorie-siden.
  - Skal ikke avhenge av produksjons-CSS for at siden skal gi mening.

### Samvirk-farger og CSS-variabler

Laboratorie-sider skal bruke **samme CSS-variabelnavn** som i `mblog.samvirk/css-template`, men med **hardkodede** verdier.

- På hver laboratorie-side defineres en `:root`-blokk i CSS med _akkurat_ disse variablene:

```css
:root {
  --first100: rgb(R1, G1, B1);
  --first80: rgba(R1, G1, B1, 0.8);
  --first50: rgba(R1, G1, B1, 0.5);
  --first20: rgba(R1, G1, B1, 0.2);
  --first10: rgba(R1, G1, B1, 0.1);
  --second100: rgb(R2, G2, B2);
  --second80: rgba(R2, G2, B2, 0.8);
  --second50: rgba(R2, G2, B2, 0.5);
  --second20: rgba(R2, G2, B2, 0.2);
  --second10: rgba(R2, G2, B2, 0.1);
}
```

- **Viktig**:
  - Bruk **kun** disse variabelnavnene for Samvirk-lignende farger på laboratorie-sider.
  - Velg `R1,G1,B1` og `R2,G2,B2` manuelt (for hver laboratorie-side).
  - Ikke generer fargene dynamisk. Ingen kall til `samvirk/load`, ingen runtime-randomisering.
  - Når du vil «endre tema» i laboratoriet, endrer du bare tallene i denne `:root`-blokken.
- I resten av CSS-en:
  - Bruk `var(--first100)`, `var(--second100)` osv. der det er naturlig:
    - F.eks. `body { background-color: var(--second100); color: var(--first100); }`

### Dynamikk og data

- **Ingen dynamikk**:
  - Ingen JS.
  - Ingen server-side data.
  - Ingen tilstand, ingen lagring.
- **Alt innhold er hardkodet**:
  - Meldinger, navn, tidsstempler, tekster – alt skrives direkte i `index.html`.
  - Skjemaer på laboratorie-sider er dekorasjon; de skal ikke sende data noe sted.

### Når du er en agent som jobber på laboratorie-sider

- **Når du lager en ny laboratorie-side**:
  - Opprett `public/laboratoriet/<navn>/index.html` og `public/laboratoriet/<navn>/styles.css`.
  - Bruk kun statisk HTML i `index.html`.
  - Sett opp en `:root`-blokk i `styles.css` med de nøyaktige Samvirk-variablene over.
  - Bygg resten av stylingen på toppen av `var(--first...)` og `var(--second...)`.

- **Når du endrer en eksisterende laboratorie-side**:
  - Behold strukturen: én mappe, én `index.html`, én `styles.css`.
  - Ikke trekk inn produksjons-CSS for å «rydde opp» – det er lov å duplisere litt i laboratoriet.
  - Ikke introducer ny dynamikk. Hold deg til HTML + CSS.

- **Når et eksperiment blir vellykket**:
  - Kopier strukturen fra laboratoriet til den ekte siden (Clojure/Hiccup + ekte CSS).
  - Tilpass til den ordentlige Samvirk-mekanismen (som genererer `:root` dynamisk).
  - Etterpå kan laboratorie-mappen:
    - enten slettes,
    - eller arkiveres tydelig (f.eks. med en kommentar i README eller lignende).

