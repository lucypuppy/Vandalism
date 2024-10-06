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

package de.nekosarekawaii.vandalism.feature.hud.impl;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.IName;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import de.nekosarekawaii.vandalism.util.render.util.GLStateTracker;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WatermarkHUDElement extends HUDElement {

    private final EnumModeValue<LogoSelection> logoSelection;
    private final IntegerValue imageWidth;
    private final IntegerValue imageHeight;

    private final ColorValue color;

    public WatermarkHUDElement(final File logoDirectory) {
        super("Watermark", true, AlignmentX.LEFT, AlignmentY.TOP, false);

        final File[] files = logoDirectory.listFiles();
        final List<LogoSelection> logoSelections = new ArrayList<>();
        logoSelections.add(new LogoSelection());
        if (files != null) {
            for (final File file : files) {
                if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                    try {
                        logoSelections.add(new LogoSelection(file));
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        this.logoSelection = new EnumModeValue<>(
                this,
                "Logo",
                "The watermark image.",
                logoSelections.get(0),
                logoSelections.toArray(new LogoSelection[0])
        );

        this.imageWidth = new IntegerValue(
                this,
                "Image Width",
                "The width of the watermark image.",
                150,
                64,
                1024
        );

        this.imageHeight = new IntegerValue(
                this,
                "Image Height",
                "The height of the watermark image.",
                150,
                64,
                1024
        );

        this.color = new ColorValue(
                this,
                "Color",
                "The color of the watermark.",
                Color.WHITE
        );
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
        final Identifier identifier = this.logoSelection.getValue().getIdentifier();
        this.mc.getTextureManager().getTexture(identifier).setFilter(true, true);
        GLStateTracker.BLEND.save(true);
        final Color selectedColor = this.color.getValue().getColor();
        context.setShaderColor(
                selectedColor.getRed() / 255f,
                selectedColor.getGreen() / 255f,
                selectedColor.getBlue() / 255f,
                selectedColor.getAlpha() / 255f
        );
        context.drawTexture(
                identifier,
                this.getX() + switch (this.alignmentX.getValue()) {
                    case MIDDLE -> -this.imageWidth.getValue() / 2;
                    case RIGHT -> -this.imageWidth.getValue();
                    default -> 0;
                },
                this.getY(),
                0,
                0,
                this.imageWidth.getValue(),
                this.imageHeight.getValue(),
                this.imageWidth.getValue(),
                this.imageHeight.getValue()
        );
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLStateTracker.BLEND.revert();
        this.width = this.imageWidth.getValue();
        this.height = this.imageHeight.getValue();
    }

    private static class LogoSelection implements IName, MinecraftWrapper {

        @Getter
        private final Identifier identifier;
        private final String name;

        public LogoSelection(final File file) throws IOException {
            this.name = file.getName();
            this.identifier = Identifier.of(FabricBootstrap.MOD_ID, "logo_" + this.name.replace(" ", "_"));
            try (final FileInputStream fileInputStream = new FileInputStream(file)) {
                this.mc.getTextureManager().registerTexture(
                        this.identifier,
                        new NativeImageBackedTexture(NativeImage.read(fileInputStream))
                );
            } catch (Exception ignored) {
            }
        }

        public LogoSelection() {
            this.identifier = FabricBootstrap.MOD_ICON;
            this.name = "Default";
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

}
