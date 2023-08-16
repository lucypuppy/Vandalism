package de.nekosarekawaii.foxglove.feature;

import de.nekosarekawaii.foxglove.util.string.EnumNameNormalizer;

public enum FeatureCategory implements EnumNameNormalizer {

    DEVELOPMENT, COMBAT, EXPLOIT, MOVEMENT, MISC, RENDER;

    private final String normalName;

    FeatureCategory() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}
