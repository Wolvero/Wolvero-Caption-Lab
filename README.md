# Wolvero Caption Lab · Android

**Wolvero Caption Lab 1.3** is een persoonlijke Android-app voor gameplayclips. Kies een video (max. 80 MiB), laat beeld en geluid analyseren, bekijk de herkende game en scène, controleer indien mogelijk het hoofdstuk via online bronnen en kies uit vijf Engelstalige captions. Geen advertenties, abonnementen of betaalmuur in de app.

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

## Wat nieuw is in 1.3

- **Herkenning per spelversie:** Gemini moet specifieke aanwijzingen zoeken (personages, locaties, UI). De app controleert versiespecifieke inconsistenties, bijvoorbeeld GTA V versus de GTA VI-locatie Leonida of het duo Jason/Lucia.
- **Extra online controle van de game**, niet enkel hoofdstukken: Tavily-zoekresultaten worden voorzichtig getoetst aan wat de AI echt in de video zag. Een correctie gebeurt enkel bij voldoende expliciete ondersteuning.
- **Betere sceneanalyse** met aparte zichtbare momenten, geluidsaanwijzingen en de emotionele of komische verhaallijn. Dit gaat vervolgens naar een zelfstandige Engelstalige caption-editor.
- **Vijf verschillende creatieve ideeën**, niet vijf variaties op dezelfde grap. De prompt vermijdt generieke captionformules en verzonnen politieachtervolgingen of gestolen auto's wanneer die niet in de video voorkomen.
- **Eenmalige schrijfinstructie zonder extra invoer**: het verhaalveld blijft volledig optioneel en standaard verborgen.

Dit is een code- en buildverbetering. De resultaten van een echte GTA VI-video moeten nog op het toestel worden beoordeeld.

## Vorige verbeteringen in 1.2

- **Video-first:** enkel een clip kiezen en op Create my captions drukken volstaat. De app analyseert zelfstandig het beeld en het geluid.
- De gametitel blijft een **vrijblijvende correctie** als automatische herkenning faalt.
- Een extra aanwijzing of verhaal is **standaard verborgen** onder een optionele uitklapknop. Je hoeft niets in te typen om captions te krijgen.
- De verborgen extra aanwijzing en gametitel worden bij het kiezen van een nieuwe clip gewist, zodat een nieuwe analyse nooit ongemerkt op de vorige video is gebaseerd.

## Eerdere verbeteringen in 1.1

- Vul optioneel **de naam van je game** in voordat je analyseert. Als je die invult, gebruikt de AI die als de gametitel in plaats van zelf te gokken.
- Onder **Het verhaal achter de clip** kun je optioneel in eigen woorden vertellen wat de grap of het emotionele moment is.
- De AI analyseert video en geluid eerst feitelijk; daarna schrijft een afzonderlijke Engelse caption-editor vijf captions met WolveroGaming-voorbeelden als stijlreferentie. Een taalcontrole probeert eventuele Nederlandse output te herstellen.
- Overbodige disclaimers en de losse 'Tip'-tekst zijn uit de app verwijderd. Het uploadgegeven in Instellingen blijft voor transparantie staan.

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
