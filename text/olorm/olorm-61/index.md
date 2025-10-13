# Kode med SI-enheter: en smakebit

Jeg (Teodor) skal presentere på online-konferanse denne uka: fredag 17. oktober, 2025.
Det er pengemessig gratis, og koster deg kun halvtimen jeg skal bruke til å snakke.

Dagens OLORM er en utfordring:
Kan budskapet i presentasjonen min tilpasses til Javascript, og kjernen formidles for Javascript-kyndig leser på fem minutter?
Jeg prøver.

Du har kanskje skrevet kode som denne:

```javascript
const iterationTimeMs = 300;
const iterations = 53;
const totalTimeSeconds = iterationTimeMs * iterations / 1000;
```

Det er vel ikke så ille?

Jo!
Jeg ser en ting som "distraherer" koden.
En [kode-leopard], om du vil.

[kode-leopard]: https://parenteser.mattilsynet.io/enkel-kode-uten-leoparder/

Fordi *-operatoren ikke kan forskjellen på millisekunder og sekunder, må vi regne ut enheten i hodet!
Den mentalgymnastikken øker [kognitiv last for oss utviklere].
Mentalgynastikken øker også sannsynligheten for feil, vi må huske på å bruke de magiske tallene (her 1000) rett for å få rett enhet.

[kognitiv last for oss utviklere]: https://2025.javazone.no/en/program/5b7b0527-6975-4718-b137-45e11b0986b0

Den samme kodesnutten kunne sett sånn ut:

```javascript
import * as munit from "munit";
import * as si from "munit/si";

const iterationTime = munit.mult(300, si.milli, si.second);
const iterations = 53;
const totalTime = munit.mult(iterationTime, iterations);
```

Bedre?

Som alltid, å trekke inn biblioteker øker "tyngden" på programmet ditt.
Og én enklelt konvertering fra millisekunder til sekunder er kanskje greit?
Jeg skrev forresten en bug i koden øverst i første versjon: `iterationTimeMs * iterations * 1000;`.
Resultatet ble én million ganger for stort!

Strukturert modellering av tall med enhet (SI eller andre ting, feks valuta) gir mer verdi jo vanskeligere problemet ditt her.
Prøv deg på kilonewton per meter, sammen med lengder i millimeter, og momenter i kilonewtonmeter!
Først med mentalgymnastikk, vanlige javascript-tall og enhetskonvertering i hodet.
Så med operasjoner som forstår tall med SI-enhet.

Det var hele smakebiten.
Kom på Macroexpand 2025 for å se resten!

Les mer eller meld deg på konferansen (penge-gratis, online) på [scicloj.github.io/macroexpand-2025/](https://scicloj.github.io/macroexpand-2025/)

Eventuelt hjelp meg å nå på verdensveven ved å [lage ståhei på Linkedin](https://www.linkedin.com/feed/update/urn:li:activity:7383440917338337280/).

Ha en fortsatt god mandag!
<br>
Hilsen Teodor
