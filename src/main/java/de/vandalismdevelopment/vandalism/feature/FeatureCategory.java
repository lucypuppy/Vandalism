package de.vandalismdevelopment.vandalism.feature;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum FeatureCategory implements EnumNameNormalizer {

    DEVELOPMENT, COMBAT, EXPLOIT, MOVEMENT, RENDER, MISC;

    private final String normalName;

    FeatureCategory() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

    public static FeatureCategory fromNormalName(final String normalName) {
        for (final FeatureCategory category : FeatureCategory.values()) {
            if (category.normalName().equalsIgnoreCase(normalName)) {
                return category;
            }
        }
        return null;
    }

}
