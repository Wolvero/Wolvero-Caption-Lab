# Wolvero Caption Lab · Android

**Wolvero Caption Lab 1.0** is een persoonlijke Android-app voor gameplayclips. Kies een video (max. 80 MiB), laat beeld en geluid analyseren, bekijk de herkende game en scène, controleer indien mogelijk het hoofdstuk via online bronnen en kies uit vijf Engelstalige captions. Geen advertenties, abonnementen of betaalmuur in de app.

## Direct de APK downloaden

[**Download Wolvero Caption Lab voor Android**](https://github.com/Wolvero/Wolvero-Caption-Lab/releases/latest/download/Wolvero-Caption-Lab.apk)

De APK wordt automatisch via GitHub Actions gebouwd en gepubliceerd als GitHub Release op deze openbare repository. Op een Android-telefoon kun je het APK-bestand downloaden, eventueel toestemming geven voor installatie uit deze bron en de app openen. Geen GitHub-login nodig voor de publieke Release.

## Eerste gebruik: twee sleutels, één keer instellen

Tik op **Instellingen** (tandwiel) of gebruik het eenmalige instelvenster bij de eerste start:

1. Plak de eigen **Gemini API key** van [Google AI Studio](https://aistudio.google.com/api-keys).
2. Plak de eigen **Tavily API key** van [Tavily](https://app.tavily.com).
3. Tik **Bewaar & start**. Daarna kies je een video en tik je op **Create my captions**.

De sleutels worden via de Android KeyStore lokaal versleuteld bewaard. Ze staan **niet in de broncode, APK of op GitHub** en zijn nergens publiek zichtbaar. Bij latere sessies hoef je ze niet meer in te vullen. Je kunt ze via het tandwiel vervangen.

De gebruiker heeft zelf reeds de sleutels gemaakt en hoeft die niet opnieuw aan te vragen.

## Hoe de analyse werkt

- De geselecteerde video wordt tijdelijk naar een private app-cache gekopieerd, tot 80 MiB.
- De Gemini Files API ontvangt de video via HTTPS. Het model `gemini-3.5-flash-lite` analyseert de beelden, schermtekst, geluid en context, en genereert vijf Engelstalige captionstijlen.
- De app probeert de waarschijnlijke game en scène te herkennen. Als die herkenbaar zijn, zoekt Tavily via één basic query naar passende walkthroughs en gamewiki's.
- Gemini vergelijkt de webresultaten met de scène. Een hoofdstuk wordt alleen als bevestigd getoond als de gevonden bronnen de precieze scène voldoende ondersteunen; anders staat er duidelijk dat een hoofdstuk onbekend of onzeker is.
- Met één tik kopieer je een caption. De video wordt na gebruik best-effort uit de Gemini Files API verwijderd.

**Het hoofdscherm bevat bewust géén 'Test verbindingen'-knop of BETA-label.** De sleutelconfiguratie zit achter het tandwiel, en bij problemen verschijnt een normale foutmelding.

## Gratis & privacy

Gebruik uitsluitend een Google API-project met **Free tier, zonder billing** en het gratis Tavily-abonnement. Gratis toegang heeft quota en kan door de providers aangepast of beperkt worden. De app heeft geen betaaloptie en kan niet verhinderen dat een gebruiker elders facturatie inschakelt.

Video's worden voor de analyse met Google gedeeld. Tavily krijgt de tekstuele zoekvraag over de game, niet het videobestand. Controleer zelf of video's geschikt zijn om naar deze diensten te verzenden. De gratis Google API-tier kan uploads gebruiken om producten te verbeteren.

## Technische details

- Android 8+; Java 17; Android Gradle Plugin 8.7.3; SDK 35
- Android KeyStore AES/GCM voor de API-sleutels
- Android-native interface zonder externe UI-libraries
- GitHub Actions bouwt de APK en publiceert die als publieke Release
- GitHub Actions levert nu een **debug-ondertekende** APK. Voor naadloze updates in toekomstige versies is een stabiele privé-signing key nodig; zonder die sleutel kan een nieuwe build herinstallatie vereisen

## Status

De Android-app is gecompileerd en als APK gepubliceerd. **Een echte upload en de API-antwoorden moeten nog op het toestel worden gevalideerd met de eigen sleutels.** AI-herkenning is probabilistisch, geen garantie op een exact hoofdstuk of viraliteit.

Bronnen: [Gemini-video](https://ai.google.dev/gemini-api/docs/video-understanding), [Gemini Files](https://ai.google.dev/gemini-api/docs/files), [Gemini pricing](https://ai.google.dev/gemini-api/docs/pricing), [Tavily API](https://docs.tavily.com/documentation/api-reference/endpoint/search).
