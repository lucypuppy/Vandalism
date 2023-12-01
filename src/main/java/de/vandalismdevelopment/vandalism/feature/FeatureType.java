package de.vandalismdevelopment.vandalism.feature;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum FeatureType implements EnumNameNormalizer {

    NONE, SCRIPT, COMMAND, MODULE;

    private final String normalName;

    FeatureType() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}
