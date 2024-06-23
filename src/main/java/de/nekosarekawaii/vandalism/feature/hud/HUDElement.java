/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.render.Buffers;
import de.nekosarekawaii.vandalism.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.render.text.AtlasFontRenderer;
import de.nekosarekawaii.vandalism.render.text.SimpleFont;
import de.nekosarekawaii.vandalism.render.text.TextAlign;
import de.nekosarekawaii.vandalism.util.common.AlignmentX;
import de.nekosarekawaii.vandalism.util.common.AlignmentY;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.awt.*;
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
    private final IntegerValue fontSize;
    private AtlasFontRenderer fontRenderer;

    @Setter
    @Getter
    protected int width, height;

    protected final EnumModeValue<AlignmentX> alignmentX;
    protected final EnumModeValue<AlignmentY> alignmentY;

    protected final IntegerValue xOffset, yOffset;

    public HUDElement(final String name, final boolean defaultActive, final AlignmentX defaultAlignmentX, final AlignmentY defaultAlignmentY) {
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
        this.fontSize = new IntegerValue(
                this,
                "Font Size",
                "The size of the font.",
                20,
                10,
                72
        ).onValueChange((oldValue, newValue) -> this.onFontSizeChange(newValue));
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
            this.fontRenderer = new AtlasFontRenderer(SimpleFont.compose(newValue, Files.readAllBytes(path.get())));
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
            case RIGHT -> x = scaledWidth - this.width - offset;
            case LEFT -> x = offset;
            default -> x = (scaledWidth - this.width) / 2;
        }
        return x + this.xOffset.getValue();
    }

    public int getY() {
        final int scaledHeight = this.mc.getWindow().getScaledHeight();
        final int offset = (this.mc.currentScreen instanceof ClientWindowScreen) ? 15 : 2;
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
        this.drawText(
                Formatting.UNDERLINE + this.getName(),
                context,
                this.getX(),
                this.getY() - this.getFontHeight() + 2,
                true,
                !inGame && !this.isActive() ? Color.RED.getRGB() : Color.WHITE.getRGB()
        );
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
        if (this.fontRenderer == null) {
            return 0;
        }
        return (int) this.fontRenderer.getFontSize() / 2;
    }

    public int getTextHeight(final String text) {
        if (this.fontRenderer == null) {
            return 0;
        }
        return (int) this.fontRenderer.getTextHeight(text, TEXT_ALIGN, this.fontSize.getValue()) / 2;
    }

    public int getTextWidth(final String text) {
        if (this.fontRenderer == null) {
            return 0;
        }
        return (int) this.fontRenderer.getTextWidth(text, TEXT_ALIGN, this.fontSize.getValue()) / 2;
    }

    protected void drawText(final String text, final DrawContext context, final float x, final float y, final boolean shadow, final int color) {
        if (this.fontRenderer == null) {
            return;
        }
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            this.fontRenderer.drawScaled(
                    text,
                    x,
                    y,
                    0.0f,
                    shadow,
                    color,
                    TEXT_ALIGN,
                    context.getMatrices().peek().getPositionMatrix(),
                    renderer,
                    null
            );
            renderer.draw();
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to draw text: {}", text, e);
        }
    }

}
