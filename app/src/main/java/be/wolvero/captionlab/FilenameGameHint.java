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

    private FilenameGameHint() {}
}
