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

package de.nekosarekawaii.vandalism.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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

    public static int getGlId(final Identifier identifier) {
        final AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(identifier);
        if (texture != null) {
            return texture.getGlId();
        } else {
            return -1;
        }
    }

    public static Color interpolateColor(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (minColor == null || midColor == null || maxColor == null) {
            throw new IllegalArgumentException("Color can't be null.");
        }
        if (percent <= 0.5) {
            return ColorUtils.colorInterpolate(minColor, midColor, MathHelper.clamp(percent * 2d, 0, 1));
        }
        return ColorUtils.colorInterpolate(midColor, maxColor, MathHelper.clamp((percent - 0.5) * 2d, 0, 1));
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

    public static void fillOutlined(final DrawContext drawContext, final int x1, final int y1, final int x2, final int y2, final int outlineWidth, final int color, final int outlineColor) {
        drawContext.fill(
                x1 + outlineWidth,
                y1 + outlineWidth,
                x2 - outlineWidth,
                y2 - outlineWidth,
                color
        );
        drawContext.fill(
                x1,
                y1,
                x2,
                y1 + outlineWidth,
                outlineColor
        );
        drawContext.fill(
                x1,
                y2 - outlineWidth,
                x2,
                y2,
                outlineColor
        );
        drawContext.fill(
                x1,
                y1 + outlineWidth,
                x1 + outlineWidth,
                y2 - outlineWidth,
                outlineColor
        );
        drawContext.fill(
                x2 - outlineWidth,
                y1 + outlineWidth,
                x2,
                y2 - outlineWidth,
                outlineColor
        );
    }

}
