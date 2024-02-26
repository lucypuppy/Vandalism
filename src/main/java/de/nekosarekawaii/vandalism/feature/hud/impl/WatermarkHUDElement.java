/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.render.GLStateTracker;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WatermarkHUDElement extends HUDElement {

    private final EnumModeValue<LogoSelection> logoSelection;
    private final IntegerValue imageWidth;
    private final IntegerValue imageHeight;

    private final ColorValue color;

    public WatermarkHUDElement(final File logoDirectory) {
        super(
                "Watermark",
                2,
                4
        );

        final File[] files = logoDirectory.listFiles();
        final LogoSelection[] logoSelections = new LogoSelection[files.length + 1];
        logoSelections[0] = new LogoSelection();

        for (int i = 0; i < files.length; i++) {
            final File file = files[i];

            if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")) {
                try {
                    logoSelections[i + 1] = new LogoSelection(file);
                } catch (IOException ignored) {
                }
            }
        }

        this.logoSelection = new EnumModeValue<>(
                this,
                "Logo",
                "The watermark image.",
                logoSelections[0],
                logoSelections
        );

        this.imageWidth = new IntegerValue(
                this,
                "Image Width",
                "The width of the watermark image.",
                156,
                64,
                1024
        );

        this.imageHeight = new IntegerValue(
                this,
                "Image Height",
                "The height of the watermark image.",
                44,
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
    public void onRender(final DrawContext context, final float delta, final boolean inGame) {
        final Identifier identifier = this.logoSelection.getValue().getIdentifier();

        this.mc.getTextureManager().getTexture(identifier).setFilter(
                true,
                true
        );
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
                this.x,
                this.y,
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

    private class LogoSelection implements IName, MinecraftWrapper {

        private final Identifier identifier;
        private final String name;

        public LogoSelection(final File file) throws IOException {
            this.name = file.getName();
            this.identifier = new Identifier(FabricBootstrap.MOD_ID, "logo_" + this.name.replace(" ", "_"));

            this.mc.getTextureManager().registerTexture(this.identifier,
                    new NativeImageBackedTexture(NativeImage.read(new FileInputStream(file))));
        }

        public LogoSelection() {
            this.identifier = FabricBootstrap.MOD_LOGO;
            this.name = "Default";
        }

        public Identifier getIdentifier() {
            return identifier;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
