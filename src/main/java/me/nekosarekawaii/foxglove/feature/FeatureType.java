package me.nekosarekawaii.foxglove.feature;

import me.nekosarekawaii.foxglove.util.EnumNameNormalizer;

/**
 * The FeatureType enum represents the types of features in the Foxglove mod.
 * It implements the EnumNameNormalizer interface to provide normalized names for the enum values.
 */
public enum FeatureType implements EnumNameNormalizer {

    /**
     * Represents the absence of a specific feature type.
     */
    NONE,

    /**
     * Represents a command feature type.
     */
    COMMAND,

    /**
     * Represents a module feature type.
     */
    MODULE;

    private final String normalName;

    /**
     * Constructor for the FeatureType enum.
     * It initializes the normalName field with the normalized name of the enum value.
     */
    FeatureType() {
        this.normalName = this.normalizeName(this.name());
    }

    /**
     * Returns the normalized name of the feature type.
     *
     * @return The normalized name.
     */
    @Override
    public String normalName() {
        return this.normalName;
    }

}
