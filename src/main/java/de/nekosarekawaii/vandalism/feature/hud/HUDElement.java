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

package de.nekosarekawaii.vandalism.feature.hud;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.text.AtlasFont;
import de.nekosarekawaii.vandalism.util.render.text.AtlasFontRenderer;
import de.nekosarekawaii.vandalism.util.render.text.SimpleFont;
import de.nekosarekawaii.vandalism.util.render.text.TextAlign;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.joml.Vector2f;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class HUDElement implements IName, ValueParent, MinecraftWrapper {

    private static final TextAlign TEXT_ALIGN = TextAlign.X_POSITIVE;

    private final String name;
    private final List<Value<?>> values;
    private final BooleanValue active;
    private AtlasFontRenderer fontRenderer;

    @Setter
    @Getter
    protected int width, height;

    protected final EnumModeValue<AlignmentX> alignmentX;
    protected final EnumModeValue<AlignmentY> alignmentY;

    protected final IntegerValue xOffset, yOffset;

    protected final BooleanValue customFont;
    private final IntegerValue fontSize;

    public HUDElement(final String name, final boolean defaultActive, final AlignmentX defaultAlignmentX, final AlignmentY defaultAlignmentY) {
        this(name, defaultActive, defaultAlignmentX, defaultAlignmentY, true);
    }

    public HUDElement(final String name, final boolean defaultActive, final AlignmentX defaultAlignmentX, final AlignmentY defaultAlignmentY, final boolean canUseCustomFont) {
        this.name = name;
        this.values = new ArrayList<>();
        this.active = new BooleanValue(
                this,
                "Active",
                "Whether this HUD element is active.",
                defaultActive
        );
        this.alignmentX = new EnumModeValue<>(
                this,
                "Alignment X",
                "The alignment of this HUD element on the x-axis.",
                defaultAlignmentX,
                AlignmentX.values()
        );
        this.alignmentY = new EnumModeValue<>(
                this,
                "Alignment Y",
                "The alignment of this HUD element on the y-axis.",
                defaultAlignmentY,
                AlignmentY.values()
        );
        this.xOffset = new IntegerValue(
                this,
                "X Offset",
                "The x offset of this HUD element.",
                0,
                -100,
                100
        );
        this.yOffset = new IntegerValue(
                this,
                "Y Offset",
                "The y offset of this HUD element.",
                0,
                -100,
                100
        );
        this.customFont = new BooleanValue(
                this,
                "Custom Font",
                "Whether to use a custom font.",
                false
        ).visibleCondition(() -> canUseCustomFont);
        this.fontSize = new IntegerValue(
                this,
                "Font Size",
                "The size of the font.",
                20,
                10,
                72
        ).onValueChange((oldValue, newValue) -> this.onFontSizeChange(newValue)).visibleCondition(() -> this.customFont.getValue() && canUseCustomFont);
        this.width = 10;
        this.height = 10;
        this.onFontSizeChange(this.fontSize.getValue());
    }

    private void onFontSizeChange(final int newValue) {
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(FabricBootstrap.MOD_ID);
        if (modContainer.isEmpty()) {
            throw new IllegalStateException("Could not find mod container of " + FabricBootstrap.MOD_ID);
        }
        final String fontName = "roboto-regular";
        final String pathString = "assets/" + FabricBootstrap.MOD_ID + "/font/" + fontName + ".ttf";
        final Optional<Path> path = modContainer.get().findPath(pathString);
        if (path.isEmpty()) {
            throw new IllegalStateException("Could not find font file: " + pathString);
        }
        try {
            final AtlasFont font = SimpleFont.compose(newValue, Files.readAllBytes(path.get()));
            if (this.fontRenderer == null) {
                this.fontRenderer = new AtlasFontRenderer(font);
            } else {
                this.fontRenderer.getFont().close();
                this.fontRenderer.setFont(font);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to load font: " + pathString, e);
        }
        this.width = 10;
        this.height = 10;
    }

    public void resetValues() {
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
    }

    public int getX() {
        final int scaledWidth = this.mc.getWindow().getScaledWidth();
        final int offset = 2;
        final int x;
        switch (this.alignmentX.getValue()) {
            case RIGHT -> x = scaledWidth - offset;
            case LEFT -> x = offset;
            default -> x = scaledWidth / 2;
        }
        return x + this.xOffset.getValue();
    }

    public int getY() {
        final int scaledHeight = this.mc.getWindow().getScaledHeight();
        final int offset = 2;
        final int y;
        switch (this.alignmentY.getValue()) {
            case BOTTOM -> {
                if (this.alignmentX.getValue() == AlignmentX.MIDDLE) {
                    y = scaledHeight - this.height - offset - this.getFontHeight() * 3;
                } else {
                    y = scaledHeight - this.height - offset;
                }
            }
            case TOP -> y = offset;
            default -> y = (scaledHeight - this.height) / 2;
        }
        return y + this.yOffset.getValue();
    }

    protected abstract void onRender(final DrawContext context, final float delta, final boolean inGame);

    public void render(final DrawContext context, final float delta, final boolean inGame) {
        this.onRender(context, delta, inGame);
    }

    public boolean isActive() {
        return this.active.getValue();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getFontHeight() {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            return this.mc.textRenderer.fontHeight;
        }
        return (int) this.fontRenderer.getFontSizeScaled();
    }

    public void getTextSize(final Text text, Vector2f dest) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            dest.set(this.mc.textRenderer.getWidth(text), this.mc.textRenderer.fontHeight);
            return;
        }
        this.fontRenderer.getTextSizeScaled(text, TEXT_ALIGN, this.fontSize.getValue(), dest);
    }

    public void getTextSize(final String text, Vector2f dest) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            dest.set(this.mc.textRenderer.getWidth(text), this.mc.textRenderer.fontHeight);
            return;
        }
        this.fontRenderer.getTextSizeScaled(text, TEXT_ALIGN, this.fontSize.getValue(), dest);
    }

    public int getTextHeight(final Text text) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            return this.mc.textRenderer.fontHeight;
        }
        return (int) this.fontRenderer.getTextHeightScaled(text, TEXT_ALIGN, this.fontSize.getValue());
    }

    public int getTextWidth(final Text text) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            return this.mc.textRenderer.getWidth(text);
        }
        return (int) this.fontRenderer.getTextWidthScaled(text, TEXT_ALIGN, this.fontSize.getValue());
    }

    public int getTextWidth(final String text) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            return this.mc.textRenderer.getWidth(text);
        }
        return (int) this.fontRenderer.getTextWidthScaled(text, TEXT_ALIGN, this.fontSize.getValue());
    }

    protected void drawText(final Text text, final DrawContext context, final float x, final float y, final boolean shadow, final int color) {
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            this.drawText(renderer, text, context, x, y, shadow, color);
            renderer.draw();
        }
    }

    protected void drawText(AttribConsumerProvider batch, final Text text, final DrawContext context, final float x, final float y, final boolean shadow, final int color) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            context.drawText(this.mc.textRenderer, text, (int) x, (int) y, color, shadow);
            return;
        }
        this.fontRenderer.drawScaled(
                text, x, y, 0.0f,
                shadow, color, TEXT_ALIGN,
                context.getMatrices().peek().getPositionMatrix(), batch, null
        );
    }

    protected void drawText(final String text, final DrawContext context, final float x, final float y, final boolean shadow, final int color) {
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            this.drawText(renderer, text, context, x, y, shadow, color);
            renderer.draw();
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to draw text: {}", text, e);
        }
    }

    protected void drawText(AttribConsumerProvider batch, final String text, final DrawContext context, final float x, final float y, final boolean shadow, final int color) {
        if (this.fontRenderer == null || !this.customFont.getValue()) {
            context.drawText(this.mc.textRenderer, text, (int) x, (int) y, color, shadow);
            return;
        }
        this.fontRenderer.drawScaled(
                text, x, y, 0.0f,
                shadow, color, TEXT_ALIGN,
                context.getMatrices().peek().getPositionMatrix(), batch, null
        );
    }

}
