package de.vandalismdevelopment.vandalism.gui.ingame;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum ElementAlignment implements EnumNameNormalizer {

    LEFT, MIDDLE, RIGHT, TOP, BOTTOM;

    private final String normalName;

    ElementAlignment() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

    public static ElementAlignment fromNormalName(final String normalName) {
        for (final ElementAlignment value : values()) {
            if (value.normalName().equals(normalName)) return value;
        }
        return null;
    }

}
