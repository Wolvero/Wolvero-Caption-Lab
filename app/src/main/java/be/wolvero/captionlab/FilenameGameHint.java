package be.wolvero.captionlab;

/**
 * Filename convention: GameNameWithoutSpaces_organization-notes.mp4
 * Only the game-name prefix ever leaves this parser.
 * The remainder is intentionally NEVER returned to any AI component.
 */
final class FilenameGameHint {
    static String extract(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        String leaf = fileName;
        int slash = Math.max(leaf.lastIndexOf('/'), leaf.lastIndexOf('\\'));
        if (slash >= 0) leaf = leaf.substring(slash + 1);
        int separator = leaf.indexOf('_');
        if (separator <= 0) return "";
        String prefix = leaf.substring(0, separator).trim();
        if (prefix.isEmpty() || prefix.length() > 100) return "";
        if (!prefix.matches("[\\p{L}\\p{N}][\\p{L}\\p{N}.'-]*")) return "";
        return prefix;
    }

    /**
     * Render a human-readable title from the creator's fixed filename game token.
     * Preserve acronyms/unknown names rather than guessing franchise or title.
     */
    static String displayTitle(String prefix) {
        if (prefix == null || prefix.isEmpty()) return "";
        return prefix.replaceAll("(?<=[a-z])(?=[A-Z])", " ")
                .replaceAll("(?<=[A-Za-z])(?=[0-9])", " ")
                .trim();
    }

    /**
     * Explicit user-entered title > creator's filename convention > model guess.
     * The file's organizational suffix is never passed into these decisions.
     */
    static String chooseTitle(String typed, String prefix, String aiGuess) {
        if (typed != null && !typed.trim().isEmpty()) return typed.trim();
        if (prefix != null && !prefix.trim().isEmpty()) return displayTitle(prefix.trim());
        return aiGuess == null ? "" : aiGuess.trim();
    }

    private FilenameGameHint() {}
}
