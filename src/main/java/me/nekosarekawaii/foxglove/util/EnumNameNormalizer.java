package me.nekosarekawaii.foxglove.util;

/**
 * The EnumNameNormalizer interface defines a contract for normalizing enum names.
 * It provides a default implementation for normalizing names by replacing underscores with spaces,
 * removing dollar signs, and converting the first character to uppercase and the rest to lowercase.
 */
public interface EnumNameNormalizer {

    /**
     * Normalizes the given enum name.
     *
     * @param name The name to normalize.
     * @return The normalized name.
     */
    default String normalizeName(String name) {
        name = name.replace("_", " ");
        name = name.replace("$", "");
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    /**
     * Gets the normalized name of the enum.
     *
     * @return The normalized name.
     */
    String normalName();

}
