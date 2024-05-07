/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

<<<<<<<<HEAD:src/main/java/de/nekosarekawaii/vandalism/util/common/Alignment.java
package de.nekosarekawaii.vandalism.util.common;

/**
 * Enum for the alignment of an object. The alignment is used to determine the position of an object relative to another object.
 */
public enum Alignment {

    LEFT, RIGHT,
    TOP, BOTTOM,

    MIDDLE

========
        package de.evilcodez.supermod.render.text;

    public abstract class GlyphInfo {

        public abstract int getCodePoint();

        public abstract boolean isUnknownGlyph();

        public abstract int getTextureId();

        public abstract int getObfuscationWidth();

        public abstract float getAdvanceX();

        public abstract float getBearingX();

        public abstract float getAscent();

        public abstract float getDescent();

        public abstract float getLineGap();

        public abstract int getOffsetX();

        public abstract int getOffsetY();

        public abstract int getWidth();

        public abstract int getHeight();

        public abstract float getU1();

        public abstract float getV1();

        public abstract float getU2();

        public abstract float getV2();
>>>>>>>>refs/heads/main:src/main/java/de/evilcodez/supermod/render/text/GlyphInfo.java
}
