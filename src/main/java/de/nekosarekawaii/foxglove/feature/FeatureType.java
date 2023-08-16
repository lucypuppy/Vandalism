package de.nekosarekawaii.foxglove.feature;

import de.nekosarekawaii.foxglove.util.string.EnumNameNormalizer;

public enum FeatureType implements EnumNameNormalizer {

    NONE, COMMAND, MODULE;

    private final String normalName;

    FeatureType() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}
