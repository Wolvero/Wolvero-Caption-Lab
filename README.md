# Wolvero Caption Lab · Android

Een echte Android-app voor TikTok-gameplaycaptions, gemaakt voor persoonlijk gebruik: videobestanden tot **80 MiB**, game-/scèneherkenning, optionele online hoofdstukverificatie, 5 captions in het Engels met variatie in toon.

## Gratis houden

Gebruik alleen een **gratis Gemini API-project zonder billing** via https://aistudio.google.com/api-keys . De app heeft geen abonnement of betaalmodule. Video- en captionanalyse gebeurt via `gemini-3.5-flash-lite`, dat een gratis API-tier heeft (met quota en mogelijke wijzigingen).

Voor **automatische online verificatie** is een tweede gratis API-sleutel aan te raden, via https://app.tavily.com . Tavily's **Researcher**-plan biedt **1.000 gratis credits per maand zonder kredietkaart**. Deze app gebruikt **één basic search (1 credit)** per video, zonder extra betaalopties of upgrades. De Tavily-resultaten worden vervolgens door de gratis Gemini-tekst-API voorzichtig op hoofdstukmatch beoordeeld. Er wordt geen hoofdstuk verzonnen als de bronnen onvoldoende zijn.

Zonder Tavily-sleutel probeert de app tijdelijk Gemini 2.5 Flash met Google Search, omdat de bestaande Google 2.5 gratis zoekquota heeft. Deze gratis zoekroute eindigt **30 november 2026**. **Vanaf 1 december 2026 schakelt de app die oude Google-zoekroute automatisch uit** om een betaalde zoekroute te vermijden. Videoherkenning en captions blijven met alleen de gratis Gemini-sleutel mogelijk; de Tavily-sleutel houdt automatische online verificatie daarna beschikbaar.

**Belangrijk:** de app kan de facturatiestatus van jouw API-projecten niet zelf controleren. Kies bij beide diensten uitsluitend de gratis tiers, schakel billing/pay-as-you-go niet in en controleer de limieten in de provideraccounts.

> Privacy: Google kan in de gratis API-tier uploads en prompts gebruiken om zijn producten te verbeteren. Video's worden voor analyse tijdelijk geüpload naar Google. Tavily ziet alleen een tekstuele zoekvraag over de game, niet jouw video. Upload niets dat je niet met die diensten wilt delen.

## Een APK bouwen zonder zelf Android Studio te installeren

1. Maak op GitHub een **nieuw leeg repository (Public mag; zet nooit sleutels in de code)** (bijvoorbeeld `Wolvero-Caption-Lab`). Geen README of .gitignore laten genereren.
2. Upload de **inhoud** van deze ZIP rechtstreeks in de hoofdmap van het nieuwe repository. Zorg dat `.github/workflows/android.yml` ook meegaat (verborgen bestanden inschakelen bij het uitpakken).
3. Open het GitHub-tabblad **Actions** → **Build Android APK**. Wacht tot de build groen is.
4. Open de voltooide run en download het artifact **Wolvero-Caption-Lab-APK**. Pak het ZIP-bestand uit, installeer `app-debug.apk` op Android.
5. Tik in de app op **API-sleutel instellen** en plak je eigen Gemini API-sleutel. Voor de automatische online hoofdstukcontrole plak je daarnaast optioneel een gratis **Tavily API-sleutel**.
6. Beide sleutels blijven versleuteld op je toestel en zitten niet ingebakken in de APK. Kies MP4/video tot 80 MiB → **Analyseer video**. Verwacht enige verwerkingstijd voor upload en videoanalyse.

### Wat gebeurt er bij analyse?

1. Kopie van geselecteerde video naar tijdelijke lokale app-cache (max. 80 MiB).
2. Resumable upload via HTTPS naar Google Gemini Files API.
3. Gemini 3.5 Flash-Lite analyseert video en audio, herkent de vermoedelijke game en scène en geeft 5 captions.
4. Als een gametitel herkenbaar is, doet Tavily (mits ingesteld) één basiszoekopdracht naar een passende gamewiki of walkthrough. Gemini beoordeelt daarna voorzichtig de gevonden bronnen. Zonder Tavily probeert de app tot 30 november 2026 de gratis Google Search in Gemini 2.5 Flash. Een hoofdstuk krijgt geen bevestigingslabel zonder bronnen.
5. Resultaten en bronlinks verschijnen in de app. Je kunt een caption tikken om te kopiëren.
6. Uploaded videobestand wordt best-effort via Files API verwijderd; de lokale cache wordt opgeschoond bij de volgende video-selectie.

## Grenzen / nog niet getest

- Dit is een eerste bouwbare **proefversie**, geen al geteste live dienst. Zonder eigen Gemini API key en Android-build kan hier geen daadwerkelijke API-respons bewezen worden.
- Herkenning is probabilistisch. Een game of exacte hoofdstuknaam is niet altijd afleidbaar uit kort gameplaymateriaal. Zoekresultaten zijn aanwijzingen, geen bewijs van een exact fragment.
- Google en Tavily kunnen gratis limieten en modelbeschikbaarheid veranderen. Videoanalyse functioneert alleen zolang de gekozen free-tier-API de calls toestaat.
- De app maakt *goede captionopties*, geen garanties op viraliteit.

## Technisch

Android 8+, Java 17, Gradle 8.10.2, Android Gradle Plugin 8.7.3, minSdk 26, compileSdk 35. Geen third-party Android dependencies, geen backend, geen hardcoded API key, alleen HTTPS-verbindingen met `generativelanguage.googleapis.com` en optioneel `api.tavily.com`.

## Bronvermeldingen

- https://ai.google.dev/gemini-api/docs/video-understanding
- https://ai.google.dev/gemini-api/docs/files
- https://ai.google.dev/gemini-api/docs/pricing
- https://ai.google.dev/gemini-api/docs/grounding
- https://docs.tavily.com/documentation/api-credits
- https://docs.tavily.com/documentation/api-reference/endpoint/search
