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

package de.nekosarekawaii.vandalism.util.render;

import de.florianmichael.rclasses.common.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class RenderUtil {

    private static double FPS, PREV_GL_TIME = Double.NaN;

    public static void drawFrame() {
        if (Double.isNaN(PREV_GL_TIME)) {
            PREV_GL_TIME = GLFW.glfwGetTime();
            return;
        }
        final double time = GLFW.glfwGetTime();
        FPS = 1.0 / (time - PREV_GL_TIME);
        PREV_GL_TIME = time;
    }

    public static double getFps() {
        return FPS;
    }

    public static Color interpolateColor(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (minColor == null || midColor == null || maxColor == null) {
            throw new IllegalArgumentException("Color can't be null.");
        }
        if (percent <= 0.5) {
            return ColorUtils.colorInterpolate(minColor, midColor, percent * 2d);
        }
        return ColorUtils.colorInterpolate(midColor, maxColor, (percent - 0.5) * 2d);
    }

    public static Formatting getRandomColor() {
        return Formatting.values()[ThreadLocalRandom.current().nextInt(1, 14)];
    }

    public static MutableText interpolateTextColor(final String text, final Color color1, final Color color2) {
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("Colors can't be null.");
        }
        final MutableText newText = Text.empty();
        final int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            final Color color = ColorUtils.colorInterpolate(color1, color2, i / (textLength - 1.0));
            newText.append(Text.literal(String.valueOf(text.charAt(i))).setStyle(Style.EMPTY.withColor(color.getRGB())));
        }
        return newText;
    }

    public static int getGlId(final Identifier identifier) {
        final AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(identifier);
        if (texture != null) {
            return texture.getGlId();
        } else {
            return -1;
        }
    }

}
