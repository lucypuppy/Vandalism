package de.foxglovedevelopment.foxglove.feature;

import de.foxglovedevelopment.foxglove.util.EnumNameNormalizer;

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
