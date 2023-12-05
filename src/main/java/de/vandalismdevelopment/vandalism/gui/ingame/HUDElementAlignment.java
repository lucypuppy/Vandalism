package de.vandalismdevelopment.vandalism.gui.ingame;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum HUDElementAlignment implements EnumNameNormalizer {

    LEFT, MIDDLE, RIGHT, TOP, BOTTOM;

    private final String normalName;

    HUDElementAlignment() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

    public static HUDElementAlignment fromNormalName(final String normalName) {
        for (final HUDElementAlignment value : values()) {
            if (value.normalName().equals(normalName)) return value;
        }
        return null;
    }

}
