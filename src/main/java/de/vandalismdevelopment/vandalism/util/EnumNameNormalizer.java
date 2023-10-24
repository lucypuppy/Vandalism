package de.vandalismdevelopment.vandalism.util;

import de.florianmichael.rclasses.common.StringUtils;

public interface EnumNameNormalizer {

    default String normalizeName(final String name) {
        return StringUtils.normalizeEnumName(name);
    }

    default String normalName() {
        return null;
    }

}
