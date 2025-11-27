# ADR 1: Arkitekturvalg  på Mikrobloggeriet

## Kontekst

Enkelte arkitekturvalg er viktige for å forstå kodebaser.
Når man ikke forstår hvorfor koden er som den er, har man to alternativer:

1. Leve med valget og aldri vite hvorfor det er sånn
2. Endre valget og potensielt ødelegge noe man ikke hadde tenkt til å ødelegge.

Det er problematisk.

## Beslutning

Vi velger å skrive disse ned som ADR-er på Mikrobloggeriet.
Les mer om ADR-er i [Documenting Architecture Decisions] av Michael Nygard.

[Documenting Architecture Decisions]: https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions.html

## Konsekvenser

Når vi gjør arkitekturbeslutninger i framtiden, skriver vi en ADR sammen med arkitekturbeslutnigen.

### Fordeler

- Å sette seg ned å skrive ADR-en gir rom til å vurdere beslutningen i ro og mak
- Personer som leser koden senere og ikke var i rommet da beslutningen ble tatt kan forstå motivasjonen for beslutningen
- Når vi endrer en beslutning, kan vi gjøre den endringen eksplisitt med en ny ADR, eller ved å endre en eksisterende ADR

### Ulemper

Det tar litt tid å skrive en ADR, og det tar litt tid å lære seg hva en god ADR er.
Den innsatsen tror jeg er verd det: når vi tar beslutninger med brede effekter, er det fint å tenke seg om.

## Alternativer

Vi kan alltids la være!
Da tar vi flere beslutninger fortere, og må leve med effektene og vagheten rundt hvordan.
