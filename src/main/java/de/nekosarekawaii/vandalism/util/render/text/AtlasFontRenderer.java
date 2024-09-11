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

package de.nekosarekawaii.vandalism.util.render.text;

import de.nekosarekawaii.vandalism.util.RandomUtils;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.Passes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector2f;

import java.util.List;

@Getter
@Setter
public class AtlasFontRenderer {

    private static final Vector2f VECTOR2F = new Vector2f();
    private final TextRendererVisitor textRendererVisitor = new TextRendererVisitor(this);
    private final TextSizeVisitor textSizeVisitor = new TextSizeVisitor(this);
    @NotNull
    private AtlasFont font;
    private boolean kerningEnabled;

    public AtlasFontRenderer(@NotNull AtlasFont font) {
        this.font = font;
    }

    public float getFontScale(float fontSize) {
        return fontSize / this.font.getFontSize();
    }

    public float draw(@Nullable String text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                      float fontSize, @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        if (text == null || text.isEmpty()) {
            if (outSize != null) outSize.set(0.0f);
            return 0.0f;
        }
        boolean needSize = true;
        if (outSize == null) {
            outSize = VECTOR2F;
            needSize = false;
        }
        boolean hasSize = true;
        if (align == TextAlign.X_CENTER) {
            this.getTextSize(text, align, fontSize, outSize);
            x -= outSize.x / 2.0f;
        } else if (align == TextAlign.X_NEGATIVE) {
            this.getTextSize(text, align, fontSize, outSize);
            x -= outSize.x;
        } else if (align == TextAlign.Y_CENTER) {
            this.getTextSize(text, align, fontSize, outSize);
            y -= outSize.y / 2.0f;
        } else if (align == TextAlign.Y_NEGATIVE) {
            this.getTextSize(text, align, fontSize, outSize);
            y -= outSize.y;
        } else hasSize = false;
        this.textRendererVisitor.init(x, y, z, shadow, color, align, fontSize, this.font.getGlyphRenderer(), batch, matrix);
        this.textSizeVisitor.init(align, fontSize, 0.0f);
        if (!hasSize && needSize) {
            TextVisitFactory.visitFormatted(text, Style.EMPTY, (index, style, cp) -> this.textRendererVisitor.accept(index, style, cp) && this.textSizeVisitor.accept(index, style, cp));
        } else {
            TextVisitFactory.visitFormatted(text, Style.EMPTY, this.textRendererVisitor);
        }
        this.textRendererVisitor.end();
        return switch (align) {
            case X_POSITIVE, X_CENTER -> this.textRendererVisitor.x - x;
            case X_NEGATIVE -> x - this.textRendererVisitor.x;
            case Y_POSITIVE, Y_CENTER -> this.textRendererVisitor.y - y;
            case Y_NEGATIVE -> y - this.textRendererVisitor.y;
        };
    }

    public float draw(@Nullable String text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                      @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, this.font.getFontSize(), matrix, batch, outSize);
    }

    /** This method draws the string in a way so the text is always the same size */
    public float drawScaled(@Nullable String text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                      float fontSize, @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), matrix, batch, outSize);
    }

    /** This method draws the string in a way so the text is always the same size */
    public float drawScaled(@Nullable String text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                            @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, this.font.getFontSize() / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), matrix, batch, outSize);
    }

    public float draw(@Nullable StringVisitable text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                      float fontSize, @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        if (text == null) {
            if (outSize != null) outSize.set(0.0f);
            return 0.0f;
        }
        boolean needSize = true;
        if (outSize == null) {
            outSize = VECTOR2F;
            needSize = false;
        }
        boolean hasSize = true;
        if (align == TextAlign.X_CENTER) {
            this.getTextSize(text, align, fontSize, outSize);
            x -= outSize.x / 2.0f;
        } else if (align == TextAlign.X_NEGATIVE) {
            this.getTextSize(text, align, fontSize, outSize);
            x -= outSize.x;
        } else if (align == TextAlign.Y_CENTER) {
            this.getTextSize(text, align, fontSize, outSize);
            y -= outSize.y / 2.0f;
        } else if (align == TextAlign.Y_NEGATIVE) {
            this.getTextSize(text, align, fontSize, outSize);
            y -= outSize.y;
        } else hasSize = false;
        this.textRendererVisitor.init(x, y, z, shadow, color, align, fontSize, this.font.getGlyphRenderer(), batch, matrix);
        this.textSizeVisitor.init(align, fontSize, 0.0f);
        if (!hasSize && needSize) {
            TextVisitFactory.visitFormatted(text, Style.EMPTY, (index, style, cp) -> this.textRendererVisitor.accept(index, style, cp) && this.textSizeVisitor.accept(index, style, cp));
        } else {
            TextVisitFactory.visitFormatted(text, Style.EMPTY, this.textRendererVisitor);
        }
        this.textRendererVisitor.end();
        return switch (align) {
            case X_POSITIVE, X_CENTER -> this.textRendererVisitor.x - x;
            case X_NEGATIVE -> x - this.textRendererVisitor.x;
            case Y_POSITIVE, Y_CENTER -> this.textRendererVisitor.y - y;
            case Y_NEGATIVE -> y - this.textRendererVisitor.y;
        };
    }

    public float draw(@Nullable StringVisitable text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                      @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, this.font.getFontSize(), matrix, batch, outSize);
    }

    /** This method draws the string in a way so the text is always the same size */
    public float drawScaled(@Nullable StringVisitable text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                            float fontSize, @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), matrix, batch, outSize);
    }

    /** This method draws the string in a way so the text is always the same size */
    public float drawScaled(@Nullable StringVisitable text, float x, float y, float z, boolean shadow, int color, @NotNull TextAlign align,
                            @NotNull Matrix4fc matrix, @NotNull AttribConsumerProvider batch, @Nullable Vector2f outSize) {
        return this.draw(text, x, y, z, shadow, color, align, this.font.getFontSize() / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), matrix, batch, outSize);
    }

    public void getTextSize(@Nullable String text, @NotNull TextAlign align, float fontSize, @NotNull Vector2f outSize) {
        if (text == null || text.isEmpty()) {
            outSize.set(0.0f);
            return;
        }
        outSize.set(0.0f);
        this.textSizeVisitor.init(align, fontSize, 0.0f);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, this.textSizeVisitor);
        outSize.set(this.textSizeVisitor.x, this.textSizeVisitor.y);
    }

    public void getTextSizeScaled(@Nullable String text, @NotNull TextAlign align, float fontSize, @NotNull Vector2f outSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), outSize);
    }

    public void getTextSize(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize, @NotNull Vector2f outSize) {
        if (text == null) {
            outSize.set(0.0f);
            return;
        }
        outSize.set(0.0f);
        this.textSizeVisitor.init(align, fontSize, 0.0f);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, this.textSizeVisitor);
        outSize.set(this.textSizeVisitor.x, this.textSizeVisitor.y);
    }

    public void getTextSizeScaled(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize, @NotNull Vector2f outSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), outSize);
    }

    public float getTextWidth(@Nullable String text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize, VECTOR2F);
        return VECTOR2F.x;
    }

    public float getTextWidthScaled(@Nullable String text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), VECTOR2F);
        return VECTOR2F.x;
    }

    public float getTextWidth(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize, VECTOR2F);
        return VECTOR2F.x;
    }

    public float getTextWidthScaled(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), VECTOR2F);
        return VECTOR2F.x;
    }

    public float getTextHeight(@Nullable String text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize, VECTOR2F);
        return VECTOR2F.y;
    }

    public float getTextHeightScaled(@Nullable String text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), VECTOR2F);
        return VECTOR2F.y;
    }

    public float getTextHeight(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize, VECTOR2F);
        return VECTOR2F.y;
    }

    public float getTextHeightScaled(@Nullable StringVisitable text, @NotNull TextAlign align, float fontSize) {
        this.getTextSize(text, align, fontSize / (float) MinecraftClient.getInstance().getWindow().getScaleFactor(), VECTOR2F);
        return VECTOR2F.y;
    }

    public float getFontSize() {
        return this.font.getFontSize();
    }

    public float getFontSizeScaled() {
        return this.font.getFontSize() / (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
    }

    /**
     * @param fontSize The font size to use
     * @return The height of the tallest character in the font
     */
    public float getFontHeight(float fontSize) {
        return this.font.getFontHeight() * this.getFontScale(fontSize);
    }

    /**
     * @param fontSize The font size to use
     * @return The width of the widest character in the font
     */
    public float getFontWidth(float fontSize) {
        return this.font.getFontWidth() * this.getFontScale(fontSize);
    }

    @NotNull
    public String substringText(@Nullable String text, @NotNull TextAlign align, float fontSize, float maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 0.0f) return "";
        this.textSizeVisitor.init(align, fontSize, maxWidth);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, this.textSizeVisitor);
        return text.substring(0, this.textSizeVisitor.charIndex);
    }

    @RequiredArgsConstructor
    private static class TextRendererVisitor implements CharacterVisitor {

        private final AtlasFontRenderer renderer;
        private float x, y, z, yOff;
        private boolean shadow;
        private int color;
        private TextAlign align;
        private float fontScale;
        private AtlasFont.GlyphRenderer glyphRenderer;
        private GlyphInfo prevGlyph;
        private AttribConsumerProvider batch;
        private AttribConsumer colorRectConsumer;

        public void init(float x, float y, float z, boolean shadow, int color, TextAlign align, float fontSize, AtlasFont.GlyphRenderer glyphRenderer, AttribConsumerProvider batch, Matrix4fc transformMatrix) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.shadow = shadow;
            this.color = color;
            this.align = align;
            this.fontScale = this.renderer.getFontScale(fontSize);
            this.glyphRenderer = glyphRenderer;
            this.prevGlyph = null;
            this.glyphRenderer.begin(batch, transformMatrix, this.fontScale);
            this.batch = batch;
            this.yOff = align.isVertical() ? 0.0f : this.renderer.font.getFontAscent() * this.fontScale;
        }

        @Override
        public boolean accept(int index, Style style, int cp) {
            GlyphInfo glyph = this.renderer.font.getGlyphInfo(cp);
            if (!this.align.isVertical() && this.renderer.kerningEnabled && this.prevGlyph != null) {
                this.increasePos(this.renderer.font.getKerning(this.prevGlyph, glyph) * this.fontScale);
            }

            int color = this.color;
            if (style.getColor() != null) {
                color = (this.color & 0xFF000000) | (style.getColor().getRgb() & 0xFFFFFF);
            }
            if (style.isObfuscated() && cp != ' ') {
                final List<? extends GlyphInfo> glyphsByWidth = this.renderer.font.getGlyphInfoByWidth(glyph.getObfuscationWidth());
                if (glyphsByWidth != null) {
                    cp = glyphsByWidth.get(RandomUtils.randomInt(glyphsByWidth.size() - 1)).getCodePoint();
                    glyph = this.renderer.font.getGlyphInfo(cp);
                }
            }
            if (this.shadow) {
                final int shadowColor = (this.color & 0xFF000000) | (((color >> 16) & 0xFF) >> 4 << 16)
                        | (((color >> 8) & 0xFF) >> 4 << 8) | ((color & 0xFF) >> 4); // color.rgb *= 0.25f;
                this.glyphRenderer.renderGlyph(glyph, this.x, this.y + this.yOff, this.z, shadowColor, true, style.isItalic(), style.isBold());
                if (style.isUnderlined()) {
                    this.underline(this.x + 1, this.y + this.yOff + 1 + 1, this.z, glyph.getAdvanceX() * this.fontScale, shadowColor);
                }
                if (style.isStrikethrough()) {
                    this.strikeThrough(this.x + 1, this.y + this.yOff + 1 + this.renderer.getFont().getFontMinY() * this.fontScale / 2.0f, this.z, glyph.getAdvanceX() * this.fontScale, shadowColor);
                }
            }
            this.glyphRenderer.renderGlyph(glyph, this.x, this.y + this.yOff, this.z, color, false, style.isItalic(), style.isBold());
            if (style.isUnderlined()) {
                this.underline(this.x, this.y + this.yOff + 1, this.z, glyph.getAdvanceX() * this.fontScale, color);
            }
            if (style.isStrikethrough()) {
                this.strikeThrough(this.x, this.y + this.yOff + this.renderer.getFont().getFontMinY() * this.fontScale / 2.0f, this.z, glyph.getAdvanceX() * this.fontScale, color);
            }

            this.increasePos(this.fontScale * (this.align.isVertical() ? glyph.getAscent() - glyph.getDescent() : glyph.getAdvanceX()));

            this.prevGlyph = glyph;
            return true;
        }

        private void fillRect(float left, float top, float right, float bottom, float z, int color) {
            if (this.colorRectConsumer == null) this.colorRectConsumer = this.batch.getAttribConsumers(Passes.colorRect()).main();
            this.colorRectConsumer.pos(left, top, z).putColor8(color).next();
            this.colorRectConsumer.pos(left, bottom, z).putColor8(color).next();
            this.colorRectConsumer.pos(right, bottom, z).putColor8(color).next();
            this.colorRectConsumer.pos(right, top, z).putColor8(color).next();
        }

        private void underline(float x, float y, float z, float width, int color) {
            this.fillRect(x, y, x + width, y + 1.5f, z, color);
        }

        private void strikeThrough(float x, float y, float z, float width, int color) {
            this.fillRect(x, y, x + width, y + 1.5f, z, color);
        }

        public void end() {
            this.glyphRenderer.end();
            this.colorRectConsumer = null;
            this.batch = null;
            this.prevGlyph = null;
        }

        private void increasePos(float amount) {
            if (this.align.isVertical()) this.y += amount;
            else this.x += amount;
        }
    }

    @RequiredArgsConstructor
    private static class TextSizeVisitor implements CharacterVisitor {

        private final AtlasFontRenderer renderer;
        private TextAlign align;
        private float x;
        private float y;
        private float fontScale;
        private GlyphInfo prevGlyph;
        private float maxWidth;
        private int charIndex;

        public void init(TextAlign align, float fontSize, float maxWidth) {
            this.align = align;
            this.x = 0.0f;
            this.y = 0.0f;
            this.fontScale = this.renderer.getFontScale(fontSize);
            this.prevGlyph = null;
            this.maxWidth = maxWidth;
            this.charIndex = 0;
        }

        @Override
        public boolean accept(int index, Style style, int cp) {
            GlyphInfo glyph = this.renderer.font.getGlyphInfo(cp);
            if (this.align.isVertical()) {
                this.x = Math.max(this.x, glyph.getAdvanceX() * this.fontScale);
                this.y += (glyph.getAscent() - glyph.getDescent()) * this.fontScale;

                if (this.maxWidth > 0.0f && this.y > this.maxWidth) return false;
            } else {
                if (this.renderer.kerningEnabled && this.prevGlyph != null) {
                    this.x += this.renderer.font.getKerning(this.prevGlyph, glyph) * this.fontScale;
                }
                this.x += glyph.getAdvanceX() * this.fontScale;
                this.y = Math.max(this.y, (glyph.getAscent() - glyph.getDescent()) * this.fontScale);

                if (this.maxWidth > 0.0f && this.x > this.maxWidth) return false;
            }
            this.charIndex = index;
            return true;
        }
    }
}
