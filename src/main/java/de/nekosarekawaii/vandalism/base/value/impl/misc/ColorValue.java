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

package de.nekosarekawaii.vandalism.base.value.impl.misc;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.HSBColor;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiMouseButton;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorValue extends Value<HSBColor> implements ValueParent, MinecraftWrapper {

    private final List<Value<?>> values = new ArrayList<>();

    @Getter
    private final EnumModeValue<ColorMode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "The mode of " + this.getName() + ".",
            ColorMode.STATIC,
            ColorMode.values()
    );

    private final IntegerValue rainbowSpeed = new IntegerValue(
            this,
            "Rainbow Speed",
            "The speed of the rainbow color mode from " + this.getName() + ".",
            2,
            1,
            10
    ).visibleCondition(() -> this.mode.getValue() == ColorMode.RAINBOW);

    private final IntegerValue twoColorSpeed = new IntegerValue(
            this,
            "Two Color Speed",
            "The speed of the two color mode from " + this.getName() + ".",
            2,
            1,
            10
    ).visibleCondition(() -> this.mode.getValue() == ColorMode.TWO_COLOR_FADE);

    private static final Color DEFAULT_MAIN_COLOR_FADE = new Color(125, 66, 246, 255);
    private static final Color DEFAULT_SECONDARY_COLOR_FADE = new Color(209, 156, 255, 255);

    private Color mainColorFade;
    private Color secondaryColorFade;

    public ColorValue(ValueParent parent, String name, String description) {
        this(parent, name, description, DEFAULT_MAIN_COLOR_FADE);
    }

    public ColorValue(ValueParent parent, String name, String description, Color defaultValue) {
        this(parent, name, description, new HSBColor(defaultValue));
    }

    public ColorValue(ValueParent parent, String name, String description, HSBColor defaultValue) {
        this(parent, name, description, defaultValue, DEFAULT_MAIN_COLOR_FADE, DEFAULT_SECONDARY_COLOR_FADE);
    }

    public ColorValue(ValueParent parent, String name, String description, HSBColor defaultValue, Color mainColorFade, Color secondaryColorFade) {
        super(parent, name, description, defaultValue);
        this.mainColorFade = mainColorFade;
        this.secondaryColorFade = secondaryColorFade;
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        final JsonObject valueNode = mainNode.get(this.getName()).getAsJsonObject();
        if (valueNode.has("color")) {
            this.setValue(new Color(valueNode.get("color").getAsInt(), true));
        }
        if (valueNode.has("mainColorFade")) {
            this.mainColorFade = new Color(valueNode.get("mainColorFade").getAsInt(), true);
        }
        if (valueNode.has("secondaryColorFade")) {
            this.secondaryColorFade = new Color(valueNode.get("secondaryColorFade").getAsInt(), true);
        }
        for (final Value<?> value : this.getValues()) {
            value.load(valueNode);
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonObject valueNode = new JsonObject();
        if (this.mode.getValue() == ColorMode.STATIC) {
            // No need to save rainbow color fading
            valueNode.addProperty("color", super.getValue().getColor().getRGB());
        }
        valueNode.addProperty("mainColorFade", this.mainColorFade.getRGB());
        valueNode.addProperty("secondaryColorFade", this.secondaryColorFade.getRGB());
        for (final Value<?> value : this.getValues()) {
            value.save(valueNode);
        }
        mainNode.add(getName(), valueNode);
    }

    public HSBColor getValue(final int offset) {
        if (this.mode.getValue() == ColorMode.RAINBOW) {
            double rainbowState = Math.ceil((System.currentTimeMillis() * this.rainbowSpeed.getValue() + offset) / 20.0);
            rainbowState %= 360.0;
            final HSBColor oldValue = super.getValue();
            oldValue.hue = (float) (rainbowState / 360.0);
            oldValue.saturation = Math.max(oldValue.saturation, 0.2f);
            oldValue.brightness = Math.max(oldValue.brightness, 0.2f);
            this.setValue(oldValue);
        } else if (this.mode.getValue() == ColorMode.TWO_COLOR_FADE) {
            this.setValue(ColorUtils.colorInterpolate(
                    this.mainColorFade,
                    this.secondaryColorFade,
                    Math.sin((System.currentTimeMillis() / 1000.0d) * this.twoColorSpeed.getValue() + offset * 0.002d) * 0.5d + 0.5d
            ));
        }
        // Do another super.getValue() because the value might have changed.
        return super.getValue();
    }

    @Override
    public HSBColor getValue() {
        return this.getValue(0);
    }

    public Color getColor(final int offset) {
        return this.getValue(offset).getColor();
    }

    public Color getColor() {
        return this.getColor(0);
    }

    public void setValue(final Color color) {
        this.setValue(new HSBColor(color));
    }

    @Override
    public void render() {
        final int colorEditFlags = ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs;
        if (this.mode.getValue() != ColorMode.TWO_COLOR_FADE) {
            final float[] rgba = ColorUtils.rgba(this.getValue().getColor().getRGB());
            if (ImGui.colorEdit4("##rgba" + this.getName() + this.getParent().getName(), rgba, colorEditFlags)) {
                this.setValue(new Color(rgba[0], rgba[1], rgba[2], Math.max(rgba[3], 0.1f)));
            }
            if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
                this.resetValue();
            }
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("The color of the color value.");
                ImGui.endTooltip();
            }
        } else {
            final float[] mainFadeRgba = ColorUtils.rgba(this.mainColorFade.getRGB());
            final float[] secondFadeRgba = ColorUtils.rgba(this.secondaryColorFade.getRGB());
            if (ImGui.colorEdit4("##mainFadeRgba" + this.getName() + this.getParent().getName(), mainFadeRgba, colorEditFlags)) {
                this.mainColorFade = new Color(mainFadeRgba[0], mainFadeRgba[1], mainFadeRgba[2], Math.max(mainFadeRgba[3], 0.1f));
            }
            if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
                this.mainColorFade = DEFAULT_MAIN_COLOR_FADE;
            }
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("The main color of the two color fade mode from the color value.");
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            if (ImGui.colorEdit4("##secondFadeRgba" + this.getName() + this.getParent().getName(), secondFadeRgba, colorEditFlags)) {
                this.secondaryColorFade = new Color(secondFadeRgba[0], secondFadeRgba[1], secondFadeRgba[2], Math.max(secondFadeRgba[3], 0.1f));
            }
            if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
                this.secondaryColorFade = DEFAULT_SECONDARY_COLOR_FADE;
            }
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("The secondary color of the two color fade mode from the color value.");
                ImGui.endTooltip();
            }
        }
        ImGui.sameLine();
        this.renderValues(false);
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public void resetValue() {
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
        this.mainColorFade = DEFAULT_MAIN_COLOR_FADE;
        this.secondaryColorFade = DEFAULT_SECONDARY_COLOR_FADE;
        super.resetValue();
    }

    public enum ColorMode implements IName {

        STATIC,
        RAINBOW,
        TWO_COLOR_FADE;

        private final String name;

        ColorMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

}
