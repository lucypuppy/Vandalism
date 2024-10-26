/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.render.util;

import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;

/**
 * Enum for the alignment of an object. The alignment is used to determine the position of an object relative to another object.
 */
public enum AlignmentX implements IName {

    LEFT, RIGHT, MIDDLE;

    private final String name;

    AlignmentX() {
        this.name = StringUtils.normalizeEnumName(this.name());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static AlignmentX getAlignmentByName(final String name) {
        for (final AlignmentX alignment : values()) {
            if (alignment.name().equalsIgnoreCase(name)) {
                return alignment;
            }
        }
        return null;
    }

}