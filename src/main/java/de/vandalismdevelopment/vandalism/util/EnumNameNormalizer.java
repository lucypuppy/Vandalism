package de.vandalismdevelopment.vandalism.util;

public interface EnumNameNormalizer {

    default String normalizeName(String name) {
        name = name.replace("_", " ");
        name = name.replace("$", "");
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    default String normalName() {
        return null;
    }

}
