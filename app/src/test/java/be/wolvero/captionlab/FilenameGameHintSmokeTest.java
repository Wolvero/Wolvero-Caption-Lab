package be.wolvero.captionlab;

public final class FilenameGameHintSmokeTest {
    private static void equals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected '" + expected + "' but got '" + actual + "'");
        }
    }

    public static void main(String[] args) {
        equals("GTAVI", FilenameGameHint.extract("GTAVI_diedinyourarms.mp4"), "GTA VI");
        equals("ResonanceAPlagueTaleLegacy",
                FilenameGameHint.extract("ResonanceAPlagueTaleLegacy_mercy.mp4"),
                "Resonance");
        equals("Grounded2", FilenameGameHint.extract("Grounded2_intro_test_1.mp4"),
                "only first underscore");
        equals("", FilenameGameHint.extract("somefile.mp4"), "without delimiter");
        equals("", FilenameGameHint.extract("_untitled.mp4"), "empty game title");
        equals("", FilenameGameHint.extract(null), "null");
        equals("Helldivers2", FilenameGameHint.extract("folder/Helldivers2_play.mp4"),
                "folder path");
        equals("", FilenameGameHint.extract("<instruction>_notes.mp4"),
                "malformed input");
        System.out.println("Filename parser checks passed (game prefix only).");
    }
}
