/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.render.text;

import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;

import java.util.List;

public abstract class AtlasFont implements AutoCloseable {

    public abstract float getFontSize();

    public abstract float getFontWidth();

    public abstract float getFontHeight();

    public abstract float getFontMinY();

    public abstract float getFontMaxY();

    public abstract float getFontAscent();

    public abstract float getFontDescent();

    public abstract float getFontLineGap();

    @NotNull
    public abstract GlyphInfo getGlyphInfo(int cp);

    @Nullable
    public abstract List<? extends GlyphInfo> getGlyphInfoByWidth(int width);

    public abstract float getKerning(@NotNull GlyphInfo glyph1, @NotNull GlyphInfo glyph2);

    public abstract GlyphRenderer getGlyphRenderer();

    @Override
    public abstract void close();

    public static abstract class GlyphRenderer {

        public abstract void begin(AttribConsumerProvider batch, Matrix4fc transformMatrix, float fontScale);

        public abstract void renderGlyph(@NotNull GlyphInfo glyph, float x, float y, float z, int color,
                                         boolean isShadowGlyph, boolean italic, boolean bold);

        public abstract void end();
    }
}
