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

package de.nekosarekawaii.vandalism.util.imgui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ImUtils {

    public static boolean subButton(final String str) {
        return ImGui.button(str, ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing());
    }

    // TODO: Wrong but fixes all guis without the need of rewriting their height values.
    public static float modulateDimension(final float input) {
        return (input / 16) * Vandalism.getInstance().getClientSettings().getMenuSettings().menuScale.getValue();
    }

    public static void loadFont(final String fontName, final int size, final ImFontAtlas atlas, final ImFontConfig fontConfig, final short[] glyphRanges) {
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(FabricBootstrap.MOD_ID);
        if (modContainer.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find mod container of {}", FabricBootstrap.MOD_ID);
            return;
        }
        final String pathString = "assets/" + FabricBootstrap.MOD_ID + "/font/" + fontName + ".ttf";
        final Optional<Path> path = modContainer.get().findPath(pathString);
        if (path.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find font file: {}", pathString);
            return;
        }
        try {
            fontConfig.setName(fontName + " " + size + "px");
            atlas.addFontFromMemoryTTF(IOUtils.toByteArray(Files.newInputStream(path.get())), size, fontConfig, glyphRanges);
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to load font: {}", pathString, e);
        }
    }

    public static void texture(final int textureId, final float textureWidth, final float textureHeight, final float x1, final float y1, final float x2, final float y2) {
        final float u1 = x1 / (textureWidth - 1f);
        final float v1 = y1 / (textureHeight - 1f);
        final float u2 = x2 / (textureWidth - 1f);
        final float v2 = y2 / (textureHeight - 1f);
        ImGui.image(textureId, textureWidth, textureHeight, u1, v1, u2, v2);
    }

}
