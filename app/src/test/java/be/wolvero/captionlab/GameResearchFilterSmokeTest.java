package be.wolvero.captionlab;

public final class GameResearchFilterSmokeTest {
    private static void yes(boolean condition, String label) {
        if (!condition) throw new AssertionError("Expected game source: " + label);
    }
    private static void no(boolean condition, String label) {
        if (condition) throw new AssertionError("Expected unrelated source: " + label);
    }
    public static void main(String[] args) {
        String query = GameResearchFilter.query("Forever Ago");
        yes(query.startsWith("\"Forever Ago\" video game"), "search anchored to title");
        no(query.contains("proposal"), "don't search generic scene keywords");
        yes(GameResearchFilter.relevant("Forever Ago",
                "Forever Ago on Steam", "https://store.steampowered.com/app/123/Forever_Ago/",
                "Adventure game storytelling"), "Steam store page");
        yes(GameResearchFilter.relevant("Forever Ago",
                "Forever Ago review", "https://www.pcgamer.com/forever-ago-review",
                "Video game storytelling"), "gaming review");
        no(GameResearchFilter.relevant("Forever Ago",
                "96 Best Ways to Propose in 2026",
                "https://example.com/marriage-proposal",
                "Romantic proposals at a diner with a ring box"), "generic proposal page");
        no(GameResearchFilter.relevant("Forever Ago",
                "Surprise proposal goes viral", "https://instagram.com/romance",
                "He proposed with a ring"), "social media unrelated");
        no(GameResearchFilter.relevant("A Plague Tale: Legacy",
                "A Plague Tale: Requiem on Steam",
                "https://store.steampowered.com/app/1325900/A_Plague_Tale_Requiem/",
                "Requiem video game"), "different game sequel");
        no(GameResearchFilter.relevant("Forever Ago",
                "Forever Ago I went camping", "https://example.com/camping",
                "An old blog"), "phrase collision");
        yes(GameResearchFilter.relevant("Grounded 2",
                "Grounded 2 video game review", "https://example.com/grounded-2",
                "Gameplay trailer"), "numeric game title");
        System.out.println("Game research filters passed.");
    }
}
