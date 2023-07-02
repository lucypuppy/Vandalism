package me.nekosarekawaii.foxglove.feature;

import me.nekosarekawaii.foxglove.util.EnumNameNormalizer;

/**
 * The FeatureCategory enum represents different categories for features in the Foxglove mod.
 * It implements the EnumNameNormalizer interface to provide a normalized name for each category.
 */
public enum FeatureCategory implements EnumNameNormalizer {

    DEVELOPMENT, COMBAT, EXPLOIT, MOVEMENT, MISC, RENDER;

    private final String normalName;

    /**
     * Constructs a FeatureCategory and initializes its normalName property by normalizing the enum name.
     */
    FeatureCategory() {
        this.normalName = this.normalizeName(this.name());
    }

    /**
     * Retrieves the normalized name of the FeatureCategory.
     *
     * @return The normalized name of the FeatureCategory.
     */
    @Override
    public String normalName() {
        return this.normalName;
    }

}
