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

package de.nekosarekawaii.vandalism.util.render.util;

import java.awt.Color;

/**
 * Represents a color in the HSB color space. This class is used to store colors in a more convenient way.
 * <p>
 * The HSB color space is a color space that describes colors in terms of hue, saturation, and brightness (also
 * called value). It is a nonlinear transformation of the RGB color space. The HSB color space is often used in
 * color selection tools, such as in Photoshop.
 * <br>
 * The HSB color space can be visualized as a double cone with the primary colors red, green, and blue at the
 * vertices of the triangle at the bottom. The brightness is represented by the vertical axis. The hue is the
 * angle of the vector from the origin to the point on the cone. The saturation is the distance from the origin.
 */
public class HSBColor {

    public float hue;
    public float saturation;
    public float brightness;

    public float alpha;

    /**
     * Creates a new HSBColor from the given color.
     *
     * @param color The color to create the HSBColor from.
     */
    public HSBColor(final Color color) {
        final float[] hsbColors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        this.hue = hsbColors[0];
        this.saturation = hsbColors[1];
        this.brightness = hsbColors[2];

        this.alpha = color.getAlpha() / 255.0F;
    }

    /**
     * Creates a new HSBColor from the given hue, saturation, brightness, and alpha values.
     *
     * @param hue        The hue of the color.
     * @param saturation The saturation of the color.
     * @param brightness The brightness of the color.
     * @param alpha      The alpha of the color.
     */
    public HSBColor(final float hue, final float saturation, final float brightness, final float alpha) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;
    }

    /**
     * @return A new {@link Color} instance with the same values as this HSBColor.
     */
    public Color getColor() {
        final Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (this.alpha * 255.0F));
    }

    /**
     * Sets the color of this HSBColor to the given color, see {@link Color}.
     *
     * @param color The color to set.
     */
    public void setColor(final Color color) {
        final float[] hsbColors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        setColor(hsbColors[0], hsbColors[1], hsbColors[2], color.getAlpha() / 255.0F);
    }

    /**
     * Sets the color of this HSBColor to the given hue, saturation, brightness, and alpha values.
     *
     * @param hue        The hue of the color.
     * @param saturation The saturation of the color.
     * @param brightness The brightness of the color.
     */
    public void setColor(final float hue, final float saturation, final float brightness) {
        setColor(hue, saturation, brightness, 1.0F);
    }

    /**
     * Sets the color of this HSBColor to the given hue, saturation, brightness, and alpha values.
     *
     * @param hue        The hue of the color.
     * @param saturation The saturation of the color.
     * @param brightness The brightness of the color.
     * @param alpha      The alpha of the color.
     */
    public void setColor(final float hue, final float saturation, final float brightness, final float alpha) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;
    }

}
