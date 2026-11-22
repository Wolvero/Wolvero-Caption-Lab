# Caption Lab · Android

**Caption Lab 1.11** is een persoonlijke Android-app voor gameplayclips. Kies een video (max. 80 MiB), laat beeld en geluid analyseren, bekijk de herkende game en scène, controleer indien mogelijk het hoofdstuk via online bronnen en kies uit vijf Engelstalige captions. Geen advertenties, abonnementen of betaalmuur in de app.

## Direct de APK downloaden

[**Download Caption Lab voor Android**](https://github.com/Wolvero/Wolvero-Caption-Lab/releases/latest/download/Caption-Lab.apk)

De APK wordt automatisch via GitHub Actions gebouwd en gepubliceerd als GitHub Release op deze openbare repository. Op een Android-telefoon kun je het APK-bestand downloaden, eventueel toestemming geven voor installatie uit deze bron en de app openen. Geen GitHub-login nodig voor de publieke Release.

## Eerste gebruik: twee sleutels, één keer instellen

Gebruik het eenmalige instelvenster bij de eerste start. Nadien kun je de sleutels zo nodig wijzigen door het CL-logo lang ingedrukt te houden:

1. Plak de eigen **Gemini API key** van [Google AI Studio](https://aistudio.google.com/api-keys).
2. Plak de eigen **Tavily API key** van [Tavily](https://app.tavily.com).
3. Tik **Bewaar & start**. Daarna kies je een video en tik je op **Create my captions**.

De sleutels worden via de Android KeyStore lokaal versleuteld bewaard. Ze staan **niet in de broncode, APK of op GitHub** en zijn nergens publiek zichtbaar. Bij latere sessies hoef je ze niet meer in te vullen. Je kunt ze zo nodig vervangen via het CL-logo.

## Wat nieuw is in 1.11

- **Lucht tussen logo en uploadkaart:** duidelijkere vaste ruimte na de CL-header.
- **Gametitelveld verwijderd:** de app gebruikt automatisch het afgesproken eerste bestandsnaamdeel vóór de underscore; de rest van de naam blijft volledig buiten de AI-captionanalyse. Het optionele veld voor eigen inhoudelijke context blijft behouden.
- **Compacte, responsieve video-invoer:** kleiner videovoorbeeld op kleinere schermen, minder lege ruimte, compacte knoppen en kaarten, en de uploadtitel verdwijnt nadat een video is gekozen. Vooral het eerste scherm mét gekozen video is erop gericht volledig op een doorsnee smartphone in portretstand te passen. Bij grote toegankelijkheidstekst of kleine toestellen kan scrollen nog nodig zijn.
- **Captiongesprek na de analyse:** onder de vijf captions kun je in een Nederlands of Engels tekstveld reageren: bijvoorbeeld 'combineer 1 en 3' of 'neem 2, maar denk meer aan ...'. De nummering verwijst naar de huidige vijf voorstellen. Eén druk op 'Verfijn mijn captions' vraagt vijf nieuwe Engelse suggesties, met de beste bovenaan.
- **Meerdere vervolgrondes:** de app onthoudt binnen dezelfde videosessie de huidige voorstellen, oorspronkelijke analyse en eerdere redactieverzoeken, zodat je verder kunt bijsturen.
- **Geen tweede videoupload of nieuwe gamezoekopdracht:** voor captionverfijning is alleen een Gemini-tekstverzoek nodig. Dat gebruikt de eigen gratis API-sleutel en quota; bij een onvolledig antwoord kan een enkele extra herstelpoging volgen.
- **Vorig resultaat blijft behouden als verfijning mislukt:** pas na succesvolle controle vervangt de app de captions. Zo raak je je eerdere ideeën niet kwijt door een mislukte Gemini-reactie.
- **Bestaande videoanalyse en game-research ongewijzigd:** de nieuwe creatieve vervolgstap is een aanvulling, geen vervanging van de huidige verwerkingsketen.

De APK-build controleert compileerbaarheid. Het responsieve resultaat op alle schermen en de bruikbaarheid van de interactieve Gemini-verfijning vereisen nog tests op een werkelijk toestel.

## Eerdere verbeteringen in 1.10

- **Gamegerichte research:** de Tavily-zoekvraag vertrekt van de exacte geselecteerde gametitel tussen aanhalingstekens en zoekt naar gamepagina's, reviews en achtergrond. Geen generieke webzoektocht op acties in de clip zoals huwelijksaanzoeken, mesgevechten of koffiescènes.
- **Bronnenfilter:** zoekresultaten worden eerst gecontroleerd op game-naam en daadwerkelijke videogamecontext; generieke huwelijksaanzoekpagina's of verwarring met een andere game komen niet meer zomaar in de resultaten. De Google Search-fallback wordt ook op bronnen gefilterd.
- **Inhoudelijk begrip:** Gemini vat ondersteunde game-achtergrond kort samen op basis van relevante bronnen. Die context komt vervolgens afzonderlijk bij de captionschrijver terecht, zonder te claimen dat game-lore gebeurtenissen in het filmpje bewijst.
- **Voorzichtig met scènematches:** een specifieke missie, personage of scène wordt alleen gekoppeld aan online informatie wanneer er een onderscheidend bewijs is, anders blijft dat veld leeg.
- **Transparante weergave:** de uitklapbare analyse toont apart game-achtergrond, bevestigde scènecontext (indien aanwezig), en daadwerkelijk relevante gamebronnen.
- **Geen extra Tavily-aanvraag:** er blijft standaard één gerichte zoekopdracht per clip. De bestaande Gemini-beoordeling haalt er de relevante gamekennis uit.
- **Geautomatiseerde regressietest:** onder meer het probleem met Forever Ago en irrelevante pagina's over echte huwelijksaanzoeken wordt getest, naast gameversies uit verschillende delen van een franchise.

Deze release is op code en build te testen; de kwaliteit van de echte zoekresultaten en captions moet nog met een gebruikersclip worden geverifieerd.

## Eerdere verbeteringen in 1.9

- **Gametitel uit de bestandsnaam is niet langer optioneel voor de AI.** Bij de bevestigde gebruikersconventie `GameTitleWithoutSpaces_organisatie.mp4` is de prefix de gekozen game. De AI kan die niet vervangen door een vergelijkbare titel in dezelfde franchise. Het handmatig ingevulde gameveld krijgt nog steeds voorrang.
- **Bewijsbronnen strikt gescheiden:** de gameprefix is uitsluitend voor de game-identiteit. De rest van de bestandsnaam wordt nooit als creatieve of inhoudelijke context doorgegeven. Beelden en geluid blijven bepalend voor de gebeurtenissen, dialogen en afloop.
- **Leesbare titel:** CamelCase in een prefix wordt veilig met spaties weergegeven (`ResonanceAPlagueTaleLegacy` wordt `Resonance A Plague Tale Legacy`). Onbekende acroniemen worden niet op goed geluk omgezet in een canonieke naam.
- **Webcontrole mag de gametitel niet overschrijven.** Ook zoekt Tavily bij een door de gebruiker geselecteerde titel expliciet binnen die game in plaats van een mogelijk foutieve AI-zoekvraag over te nemen.
- **Voorzichtiger scèneanalyse:** een getrokken mes bewijst niet automatisch een voltooide steek; zonder duidelijk bewijs worden namen van personages en de afloop niet uit franchisekennis verzonnen.
- De uitgebreide parser-smoketest controleert automatisch de titelprioriteit, inclusief precies het geval waarin het model ten onrechte `A Plague Tale: Requiem` kiest.

De titelprioriteit is technisch getest, maar de inhoudelijke nauwkeurigheid van de videoanalyse moet met echte clips beoordeeld worden.

## Eerdere verbeteringen in 1.8

- **Scènegestuurde creatieve keuze:** eerst het hele fragment begrijpen, dan de juiste toon kiezen, vervolgens vijf sterke captions schrijven en de feitelijke afloop controleren.
- **Geen vast lijstje van vijf stijlen:** de AI kiest een passende, natuurlijke toon in plaats van verplicht een grappige, emotionele, filmische en virale caption te leveren.
- **Minder AI-achtige teksten:** eigenzinnig, vlot en menselijk Engels; droog wanneer het grappig is, uitgesproken wanneer het verdient, ingetogen wanneer het raakt.
- **Meer kans op aandacht zonder clickbait:** een eerlijke hook uit de beelden, het geluid, een verrassing, de emotie of een onverwacht contrast; geen verzonnen situaties of claims over viraliteit.
- **Sterkste caption eerst**, met vier hoogwaardige alternatieven. De app geeft de gekozen creatieve richting in het uitklapbare analyseblok weer.
- **Zelfde stijl in herstel- en verificatiestappen:** die mogen de oorspronkelijke scènegebonden toon niet vervangen door een sjabloon.
- **Geen extra API-verzoek:** de toonkeuze en vijf captions komen uit dezelfde Gemini-generatieaanvraag.

De APK-build controleert technisch de code. De kwaliteit van Gemini-uitvoer en de kans op bereik zijn niet gegarandeerd.

## Eerdere verbeteringen in 1.7

- **Vaste bestandsnaamconventie:** `GametitelZonderSpaties_omschrijving.mp4`. Alleen het deel vóór de **eerste underscore** is een extra, automatisch gevonden hint voor de gametitel.
- **De tekst na de eerste underscore wordt volledig genegeerd voor alle AI-aanvragen.** Het is uitsluitend een organisatorische bestandsaanduiding en zegt niets over wat de video moet betekenen of welke caption de app hoort te maken. Bijvoorbeeld: `ResonanceAPlagueTaleLegacy_mercy.mp4` levert enkel `ResonanceAPlagueTaleLegacy` als hint aan de gamedetector; het woord na de underscore wordt nergens meegestuurd.
- De gametitelhint is geen bewijs; de video- en audioanalyse blijft leidend. Een expliciet ingevulde gametitel krijgt voorrang.
- Er is een Java-smoketest toegevoegd voor het parsergedrag, die automatisch voor elke Android-build draait.
- **Geen extra invoer nodig:** de gebruiker kiest alleen een video. Optionele makerstoelichting blijft volledig los van de bestandsnaam.

## Eerdere verbeteringen in 1.6

- **Foutcorrectie voor onvolledige AI-antwoorden.** De vorige versie verwierp het volledige resultaat als een van de AI-antwoorden minder dan exact vijf captions bevatte. Dat kon ook gebeuren tijdens de extra controle van een optionele makerstoelichting.
- De app kan nu **1–5 unieke, bruikbare captions** tijdelijk vasthouden en vanuit de bestaande geanalyseerde videofeiten een ontbrekende set laten aanvullen, zonder de videofile opnieuw naar Gemini te uploaden.
- De semantische contextcontrole probeert een onvoldoende of verkeerd gevormd AI-antwoord via een extra beknopte aanvraag te herstellen. De controle blijft gericht op het behoud van de werkelijk bedoelde afloop, niet op een bepaald onderwerp.
- Als Google ook na herstelpogingen minder dan vijf captions levert, toont de app **het werkelijke aantal** in plaats van een harde crash of verzonnen opvulteksten.
- Extra API-verzoeken voor herstel zijn alleen nodig als een model minder dan vijf of anderszins onbruikbare captions terugstuurt. Gratis gebruik blijft afhankelijk van de quota van de eigen Gemini API-sleutel.

De APK compileert via GitHub Actions; het daadwerkelijke Gemini-antwoord is hiermee nog niet live gevalideerd.

## Eerdere verbeteringen in 1.5

- Naam, app-label, launcher-icon, logo en zichtbare branding zijn **Caption Lab**. De bestaande technische Android package-id blijft behouden, zodat toekomstige updates dezelfde app-identiteit gebruiken.
- Videoanalyse legt **de zichtbare afloop** expliciet vast, met eventuele onzekerheid in het videobeeld.
- Een ingevulde, optionele auteurscontext wordt via een **aparte semantische interpretatie** verwerkt: wie doet wat, welke handeling gebeurt of net niet, en wat is de bedoelde afloop?
- De captionschrijver ontvangt zowel de feitelijke videoanalyse als het apart gelabelde auteurscommentaar. De notitie is niet verplicht.
- Een extra onafhankelijke AI-redactie controleert bij ingevulde context of de vijf captions de kernhandeling of afloop impliciet of expliciet tegenspreken. Deze algemene consistentiecontrole is **niet specifiek voor genade** of voor een game geschreven.
- De extra API-calls voor notities verbruiken meer van het gratis Gemini-quotum. Zonder notitie blijft de normale videoworkflow behouden.

De werking van de semantische kwaliteitscontrole moet nog aan de hand van echte clips worden geëvalueerd. Een geslaagde build bewijst alleen dat de app compileert.

## Eerdere verbeteringen in 1.4

- **Een interface die met je mee verandert:** op het startscherm zie je het uploadverhaal; zodra je een video selecteert verdwijnen de intro en informatiechips. Je ziet de gekozen clip, het optionele gameveld en de knop om captions te maken. Zodra er resultaten zijn, verdwijnen de uploadblokken en komen de captions meteen bovenaan.
- **Geen permanent sleutel-tandwiel** wanneer beide sleutels zijn opgeslagen. Je kunt sleutels indien nodig nog wijzigen door het **W/-logo lang ingedrukt** te houden. Bij ontbrekende sleutels of bepaalde foutgevallen kan de configuratie uiteraard terug verschijnen.
- **Videoanalyse en bronnen zijn uitklapbaar** onder de vijf captions. De eindgebruiker krijgt geen onnodige hoofdstukfout als headline bij trailers of montages.
- **Montages en emotionele samenhang:** de videoanalist benoemt het totale thema en de vorm van het filmpje (voorzichtig, als inschatting), en de captionschrijver zoekt de betekenis van het volledige fragment in plaats van vijf losse zinnen over vijf shots.
- De daadwerkelijk door de maker gepubliceerde caption **"In a world of chaos, there's still love ♡"** is als specifieke stijlreferentie toegevoegd voor emotionele GTA VI-montages, zonder dit tot standaardformule voor andere clips te verheffen.
- De scrollbar werd aangepast zodat resultatentitels beter wegblijven uit de systeemstatusbalk.

De Android-build controleert of de code compileert. Of de captionkeuze goed aansluit op een individuele video, vereist nog een praktijktest.

## Eerdere verbeteringen in 1.3

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
- De AI analyseert video en geluid eerst feitelijk; daarna schrijft een afzonderlijke Engelse caption-editor vijf captions met gaming creator-voorbeelden als stijlreferentie. Een taalcontrole probeert eventuele Nederlandse output te herstellen.
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
