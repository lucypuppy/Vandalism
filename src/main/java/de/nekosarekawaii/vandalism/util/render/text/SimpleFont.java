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

import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.Passes;
import de.nekosarekawaii.vandalism.util.render.gl.texture.InternalTextureFormat;
import de.nekosarekawaii.vandalism.util.render.gl.texture.Texture2D;
import de.nekosarekawaii.vandalism.util.render.gl.texture.TextureFormat;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.DataType;
import de.nekosarekawaii.vandalism.util.render.util.RectPacker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_FreeBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

public class SimpleFont extends AtlasFont {

    private static final int ATLAS_WIDTH = 2048;
    private static final int ATLAS_HEIGHT = 2048;

    private final float fontSize;
    protected final RectPacker packer;
    protected final Vector2i packerOffsets;
    protected final List<STBTTFontinfo> fontInfos;
    protected final List<ByteBuffer> fontBuffers;
    protected final List<Texture2D> atlasTextures;
    private final List<float[][]> kerningTables;
    private final SimpleGlyph[] asciiGlyphs;
    private final Int2ObjectMap<SimpleGlyph> glyphMap;
    protected final Int2ObjectMap<List<SimpleGlyph>> glyphsByAdvance;
    private final SimpleGlyphRenderer glyphRenderer;
    private float widestGlyph;
    private float fontHeight;
    private float fontMinY, fontMaxY;
    private float fontAscent, fontDescent, fontLineGap;

    private SimpleFont(float fontSize) {
        this.fontSize = fontSize;
        this.packer = new RectPacker();
        this.packerOffsets = new Vector2i();
        this.fontInfos = new ArrayList<>();
        this.fontBuffers = new ArrayList<>();
        this.atlasTextures = new ArrayList<>();
        this.kerningTables = new ArrayList<>();
        this.asciiGlyphs = new SimpleGlyph[256];
        this.glyphMap = new Int2ObjectOpenHashMap<>();
        this.glyphsByAdvance = new Int2ObjectOpenHashMap<>();
        this.glyphRenderer = new SimpleGlyphRenderer();
    }

    public SimpleFont(float fontSize, byte[] ttfData) {
        this(fontSize);
        boolean success = false;
        try {
            this.addFont(ttfData);
            this.resetFont();
            success = true;
        } finally {
            if (!success) this.close();
        }
    }

    public static SimpleFont compose(float fontSize, Iterable<byte[]> ttfData) {
        final SimpleFont font = new SimpleFont(fontSize);
        boolean success = false;
        try {
            for (byte[] data : ttfData) {
                font.addFont(data);
            }
            if (font.fontInfos.isEmpty()) throw new IllegalArgumentException("No font data provided");
            font.resetFont();
            success = true;
        } finally {
            if (!success) font.close();
        }
        return font;
    }

    public static SimpleFont compose(float fontSize, byte[]... ttfData) {
        if (ttfData.length == 0) throw new IllegalArgumentException("No font data provided");
        final SimpleFont font = new SimpleFont(fontSize);
        boolean success = false;
        try {
            for (byte[] data : ttfData) {
                font.addFont(data);
            }
            font.resetFont();
            success = true;
        } finally {
            if (!success) font.close();
        }
        return font;
    }

    private void addFont(byte[] ttfData) {
        final ByteBuffer buffer = MemoryUtil.memAlloc(ttfData.length);
        buffer.put(ttfData).flip();
        final STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, buffer)) {
            throw new RuntimeException("Failed to parse font data");
        }
        this.fontInfos.add(fontInfo);
        this.fontBuffers.add(buffer);

        final float[][] kerningTable = new float[256][256];
        for (int i = 0; i < kerningTable.length; i++) {
            final float[] column = kerningTable[i];
            for (int j = 0; j < column.length; j++) {
                column[j] = stbtt_GetCodepointKernAdvance(fontInfo, i, j);
            }
        }
        this.kerningTables.add(kerningTable);
    }

    public void resetFont() {
        for (Texture2D atlas : this.atlasTextures) {
            atlas.close();
        }
        this.atlasTextures.clear();
        this.glyphMap.clear();
        Arrays.fill(this.asciiGlyphs, null);
        this.widestGlyph = 0.0f;

        this.fontHeight = 0.0f;
        this.fontMinY = 0.0f;
        this.fontMaxY = 0.0f;
        for (int i = 0; i < 1024; i++) { // preload the first 1024 glyphs
            final SimpleGlyph glyph = (SimpleGlyph) this.getGlyphInfo(i);
            if (i > 32 && i < 127) {
                if (glyph.getHeight() > this.fontHeight) this.fontHeight = glyph.getHeight();
                if (glyph.getOffsetY() < this.fontMinY) this.fontMinY = glyph.getOffsetY();
                if (glyph.getOffsetY() + glyph.getHeight() > this.fontMaxY) this.fontMaxY = glyph.getOffsetY() + glyph.getHeight();
            }
        }
        final int[] ascent = new int[1], descent = new int[1], lineGap = new int[1];
        stbtt_GetFontVMetrics(this.fontInfos.get(0), ascent, descent, lineGap);
        final float fontScale = stbtt_ScaleForPixelHeight(this.fontInfos.get(0), this.fontSize);
        this.fontAscent = ascent[0] * fontScale;
        this.fontDescent = descent[0] * fontScale;
        this.fontLineGap = lineGap[0] * fontScale;
    }

    @Override
    public float getFontSize() {
        return this.fontSize;
    }

    @Override
    public float getFontWidth() {
        return this.widestGlyph;
    }

    @Override
    public float getFontHeight() {
        return this.fontHeight;
    }

    @Override
    public float getFontMinY() {
        return this.fontMinY;
    }

    @Override
    public float getFontMaxY() {
        return this.fontMaxY;
    }

    @Override
    public float getFontAscent() {
        return this.fontAscent;
    }

    @Override
    public float getFontDescent() {
        return this.fontDescent;
    }

    @Override
    public float getFontLineGap() {
        return this.fontLineGap;
    }

    @Override
    public @NotNull GlyphInfo getGlyphInfo(int cp) {
        if (cp >= 0 && cp < 256) {
            if (this.asciiGlyphs[cp] == null) {
                final SimpleGlyph glyph = this.createGlyph(cp);
                this.asciiGlyphs[cp] = glyph;
                this.glyphMap.put(cp, glyph);
                if (!glyph.isUnknownGlyph()) {
                    this.glyphsByAdvance.computeIfAbsent(glyph.getObfuscationWidth(), k -> new ArrayList<>()).add(glyph);
                }
            }
            return this.asciiGlyphs[cp];
        }
        SimpleGlyph glyph = this.glyphMap.get(cp);
        if (glyph == null) {
            glyph = this.createGlyph(cp);
            this.glyphMap.put(cp, glyph);
            if (!glyph.isUnknownGlyph()) {
                this.glyphsByAdvance.computeIfAbsent(glyph.getObfuscationWidth(), k -> new ArrayList<>()).add(glyph);
            }
        }
        return glyph;
    }

    @Override
    public @Nullable List<? extends GlyphInfo> getGlyphInfoByWidth(int width) {
        return this.glyphsByAdvance.get(width);
    }

    @Override
    public float getKerning(@NotNull GlyphInfo glyph1, @NotNull GlyphInfo glyph2) {
        final SimpleGlyph g1 = (SimpleGlyph) glyph1;
        final SimpleGlyph g2 = (SimpleGlyph) glyph2;
        if (g1.fontIndex != g2.fontIndex) return 0.0f;
        if (g1.codepoint >= 256 || g2.codepoint >= 256 || g1.codepoint < 0 || g2.codepoint < 0) {
            return this.kerningTables.get(g1.fontIndex)[g1.codepoint][g2.codepoint];
        }
        return stbtt_GetCodepointKernAdvance(this.fontInfos.get(g1.fontIndex), g1.codepoint, g2.codepoint);
    }

    @Override
    public GlyphRenderer getGlyphRenderer() {
        return this.glyphRenderer;
    }

    @Override
    public void close() {
        this.atlasTextures.forEach(Texture2D::close);
        this.atlasTextures.clear();
        this.fontInfos.clear();
        this.fontBuffers.forEach(MemoryUtil::memFree);
        this.fontBuffers.clear();
    }

    protected @NotNull SimpleGlyph createGlyph(int cp) {
        final int[] xOff = new int[1], yOff = new int[1];
        final int[] width = new int[1], height = new int[1];
        final int[] advX = new int[1];
        final int[] bearingX = new int[1], ascent = new int[1], descent = new int[1], lineGap = new int[1];
        float scale = 0.0f;
        int firstXOff = 0, firstYOff = 0;
        int firstWidth = 0, firstHeight = 0;
        int firstAdvX = 0, firstBearingX = 0;
        int firstAscent = 0, firstDescent = 0, firstLineGap = 0;
        ByteBuffer firstBuf = null;
        float firstScale = 0.0f;
        int fontIndex = -1;
        ByteBuffer glyphBuf = null;
        for (int i = 0; i < this.fontInfos.size(); i++) {
            final STBTTFontinfo fontInfo = this.fontInfos.get(i);
            scale = stbtt_ScaleForPixelHeight(fontInfo, this.fontSize);
            final int glyphIndex = stbtt_FindGlyphIndex(fontInfo, cp);
            if (i != 0 && glyphIndex == 0) continue;
            glyphBuf = stbtt_GetGlyphBitmap(fontInfo, scale, scale, glyphIndex, width, height, xOff, yOff);
            stbtt_GetGlyphHMetrics(fontInfo, glyphIndex, advX, bearingX);
            stbtt_GetFontVMetrics(fontInfo, ascent, descent, lineGap);

            if (i == 0) {
                firstXOff = xOff[0]; firstYOff = yOff[0];
                firstWidth = width[0]; firstHeight = height[0];
                firstAdvX = advX[0]; firstBearingX = bearingX[0];
                firstAscent = ascent[0]; firstDescent = descent[0]; firstLineGap = lineGap[0];
                firstBuf = glyphBuf;
                firstScale = scale;
            }
            if (glyphBuf != null) {
                if (glyphIndex == 0) {
                    glyphBuf = null; // glyphBuf will be freed in the code below
                    continue;
                }
                fontIndex = i;
                break;
            }
        }
        boolean unknown = false;
        if (fontIndex == -1) { // No glyphs found in any font, so we're using the first font
            unknown = true;
            xOff[0] = firstXOff; yOff[0] = firstYOff;
            width[0] = firstWidth; height[0] = firstHeight;
            advX[0] = firstAdvX; bearingX[0] = firstBearingX;
            ascent[0] = firstAscent; descent[0] = firstDescent; lineGap[0] = firstLineGap;
            glyphBuf = firstBuf;
            scale = firstScale;
            fontIndex = 0;
        } else if (firstBuf != null && fontIndex != 0) { // Free the first buffer if it's not being used
            stbtt_FreeBitmap(firstBuf);
        }

        int textureIndex = this.atlasTextures.size() - 1;
        if (this.atlasTextures.isEmpty()) {
            this.atlasTextures.add(this.createTexture());
            this.packer.init(ATLAS_WIDTH, ATLAS_HEIGHT);
            textureIndex = 0;
        }

        if (!this.packer.packSingleRect(width[0] + 1, height[0] + 1, this.packerOffsets)) {
            this.atlasTextures.add(this.createTexture());
            this.packer.init(ATLAS_WIDTH, ATLAS_HEIGHT);
            this.packerOffsets.set(0, 0);
            if (!this.packer.packSingleRect(width[0] + 1, height[0] + 1, this.packerOffsets)) {
                if (glyphBuf != null) stbtt_FreeBitmap(glyphBuf);
                throw new RuntimeException("Failed to pack glyph into texture atlas");
            }
            ++textureIndex;
        }

        if (glyphBuf != null) {
            this.atlasTextures.get(textureIndex).upload(0, this.packerOffsets.x, this.packerOffsets.y, width[0], height[0], TextureFormat.RED, DataType.UNSIGNED_BYTE, glyphBuf);
            stbtt_FreeBitmap(glyphBuf);
        }

        final float u1 = (float) this.packerOffsets.x / ATLAS_WIDTH;
        final float v1 = (float) this.packerOffsets.y / ATLAS_HEIGHT;
        final float u2 = (float) (this.packerOffsets.x + width[0]) / ATLAS_WIDTH;
        final float v2 = (float) (this.packerOffsets.y + height[0]) / ATLAS_HEIGHT;

        return new SimpleGlyph(
                this, cp, unknown, fontIndex, textureIndex,
                advX[0] * scale, advX[0], bearingX[0] * scale,
                ascent[0] * scale, descent[0] * scale, lineGap[0] * scale,
                xOff[0], yOff[0], width[0], height[0],
                u1, v1, u2, v2
        );
    }

    protected Texture2D createTexture() {
        final Texture2D texture = new Texture2D(1, InternalTextureFormat.R8, ATLAS_WIDTH, ATLAS_HEIGHT);
        texture.setFilter(GL45C.GL_LINEAR);
        texture.setWrap(GL45C.GL_CLAMP_TO_BORDER);
        texture.setBorderColor(0.0f, 0.0f, 0.0f, 0.0f);
        texture.clear(0);
        return texture;
    }

    private static class SimpleGlyphRenderer extends GlyphRenderer {

        private final Vector3f transformVec = new Vector3f();
        protected AttribConsumerProvider batch;
        protected Matrix4fc transform;
        protected float fontScale;
        protected AttribConsumer consumer;
        protected int lastTexture;

        @Override
        public void begin(AttribConsumerProvider batch, Matrix4fc transformMatrix, float fontScale) {
            this.batch = batch;
            this.transform = transformMatrix;
            this.fontScale = fontScale;
            this.lastTexture = 0;
        }

        @Override
        public void renderGlyph(@NotNull GlyphInfo glyph, float x, float y, float z, int color, boolean isShadowGlyph, boolean italic, boolean bold) {
            if (this.lastTexture != glyph.getTextureId() || this.consumer == null) {
                this.consumer = this.batch.getAttribConsumers(Passes.text(glyph.getTextureId())).main();
                this.lastTexture = glyph.getTextureId();
            }

            final float offset = isShadowGlyph ? 1.0f : 0.0f;
            final float italicOffset = italic ? this.getItalicOffset((SimpleGlyph) glyph) : 0.0f;
            final float x1 = x + glyph.getOffsetX() * this.fontScale;
            final float y1 = y + glyph.getOffsetY() * this.fontScale;
            final float x2 = x1 + glyph.getWidth() * this.fontScale;
            final float y2 = y1 + glyph.getHeight() * this.fontScale;
            this.consumer.pos(this.transform(x1 + italicOffset, y1, z, offset)).putUV(glyph.getU1(), glyph.getV1()).putColor8(color).next();
            this.consumer.pos(this.transform(x1, y2, z, offset)).putUV(glyph.getU1(), glyph.getV2()).putColor8(color).next();
            this.consumer.pos(this.transform(x2, y2, z, offset)).putUV(glyph.getU2(), glyph.getV2()).putColor8(color).next();
            this.consumer.pos(this.transform(x2 + italicOffset, y1, z, offset)).putUV(glyph.getU2(), glyph.getV1()).putColor8(color).next();
        }

        @Override
        public void end() {
            this.batch = null;
            this.transform = null;
            this.fontScale = 0.0f;
            this.consumer = null;
            this.lastTexture = 0;
        }

        private Vector3f transform(float x, float y, float z, float offset) {
            this.transformVec.set(x, y, z);
            this.transform.transformPosition(this.transformVec);
            this.transformVec.x += offset;
            this.transformVec.y += offset;
            return this.transformVec;
        }

        protected float getItalicOffset(SimpleGlyph glyph) {
            return glyph.owner.fontHeight * this.fontScale * 0.15f;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    protected static class SimpleGlyph extends GlyphInfo {
        final SimpleFont owner;
        final int codepoint;
        final boolean unknown;
        final int fontIndex;
        final int textureIndex;
        final float advanceX;
        final int originalAdvanceX;
        final float bearingX;
        final float ascent;
        final float descent;
        final float lineGap;
        final int offsetX;
        final int offsetY;
        final int width;
        final int height;
        final float u1;
        final float v1;
        final float u2;
        final float v2;

        @Override
        public int getCodePoint() {
            return this.codepoint;
        }

        @Override
        public boolean isUnknownGlyph() {
            return this.unknown;
        }

        @Override
        public int getTextureId() {
            return this.owner.atlasTextures.get(this.textureIndex).id();
        }

        @Override
        public int getObfuscationWidth() {
            return this.originalAdvanceX;
        }

        @Override
        public float getAdvanceX() {
            return this.advanceX;
        }

        @Override
        public float getBearingX() {
            return this.bearingX;
        }

        @Override
        public float getAscent() {
            return ascent;
        }

        @Override
        public float getDescent() {
            return descent;
        }

        @Override
        public float getLineGap() {
            return lineGap;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public float getU1() {
            return this.u1;
        }

        @Override
        public float getV1() {
            return this.v1;
        }

        @Override
        public float getU2() {
            return this.u2;
        }

        @Override
        public float getV2() {
            return this.v2;
        }
    }
}
