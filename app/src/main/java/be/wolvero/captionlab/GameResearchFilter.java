package be.wolvero.captionlab;

import java.util.Locale;

/** Reject off-topic web search results before they can influence the caption model. */
final class GameResearchFilter {
    static String query(String gameTitle) {
        String clean = gameTitle == null ? "" :
                gameTitle.replaceAll("[^\\p{L}\\p{N}\\s:.'-]", " ")
                        .replaceAll("\\s+", " ").trim();
        if (clean.isEmpty()) return "videogame story official review";
        if (clean.length() > 85) clean = clean.substring(0, 85).trim();
        return "\"" + clean + "\" video game official Steam gameplay story review walkthrough";
    }

    static boolean relevant(String selectedGame, String title, String url, String excerpt) {
        String game = norm(selectedGame);
        if (game.isEmpty() || game.equals("unknown") || game.length() < 3) return false;
        String heading = norm(title);
        String pageUrl = norm(url);
        String body = norm(excerpt);
        String together = heading + " " + pageUrl + " " + body;
        boolean exactTitle = containsPhrase(heading, game);
        String gameCompact = game.replace(" ", "");
        boolean exactUrl = gameCompact.length() >= 5 &&
                pageUrl.replace(" ", "").contains(gameCompact);
        boolean excerptMatch = containsPhrase(body, game) && gamingDomain(pageUrl);
        return gamingContext(together) && (exactTitle || exactUrl || excerptMatch);
    }

    private static boolean gamingDomain(String url) {
        return url.contains("steampowered") || url.contains("steamcommunity") ||
                url.contains("playstation") || url.contains("xbox") ||
                url.contains("nintendo") || url.contains("ign com") ||
                url.contains("gamespot") || url.contains("gamepressure") ||
                url.contains("game8") || url.contains("wiki gg") ||
                url.contains("pcgamer") || url.contains("kotaku") ||
                url.contains("eurogamer") || url.contains("rpgsite") ||
                url.contains("fandom com") || url.contains("gamerant") ||
                url.contains("polygon com");
    }

    private static boolean gamingContext(String text) {
        return containsPhrase(text, "video game") ||
                containsPhrase(text, "videogame") ||
                containsPhrase(text, "gameplay") ||
                containsPhrase(text, "walkthrough") ||
                containsPhrase(text, "steam") ||
                containsPhrase(text, "playstation") ||
                containsPhrase(text, "xbox") ||
                containsPhrase(text, "nintendo") ||
                containsPhrase(text, "developer") ||
                containsPhrase(text, "game review") ||
                containsPhrase(text, "gaming") ||
                containsPhrase(text, "gamespot") ||
                containsPhrase(text, "wiki gg") ||
                containsPhrase(text, "gamepressure") ||
                containsPhrase(text, "pc gamer") ||
                containsPhrase(text, "ign com") ||
                containsPhrase(text, "rpgsite") ||
                containsPhrase(text, "game8") ||
                containsPhrase(text, "eurogamer") ||
                containsPhrase(text, "fandom com");
    }

    private static boolean containsPhrase(String haystack, String needle) {
        return !needle.isEmpty() && (" " + haystack + " ").contains(" " + needle + " ");
    }

    private static String norm(String input) {
        if (input == null) return "";
        return input.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .replaceAll("\\s+", " ").trim();
    }

    private GameResearchFilter() {}
}
