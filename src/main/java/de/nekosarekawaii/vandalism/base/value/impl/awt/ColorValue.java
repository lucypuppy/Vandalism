package de.nekosarekawaii.vandalism.base.value.impl.awt;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.ColorUtils;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiDataType;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.awt.*;

public class ColorValue extends Value<Color> {

    private boolean rainbowMode = false;
    private float rainbowSaturation = 0.6f;
    private float rainbowBrightness = 1f;
    private int rainbowSpeed = 2;

    public ColorValue(ValueParent parent, String name, String description, Color defaultValue) {
        super(parent, name, description, defaultValue);
    }

    public ColorValue(ValueParent parent, String name, String description, Color defaultValue,
                      boolean rainbowMode, float rainbowSaturation, float rainbowBrightness, int rainbowSpeed) {
        super(parent, name, description, defaultValue);
        this.rainbowMode = rainbowMode;
        this.rainbowSaturation = rainbowSaturation;
        this.rainbowBrightness = rainbowBrightness;
        this.rainbowSpeed = rainbowSpeed;
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(getName())) {
            return;
        }

        final JsonObject valueNode = mainNode.get(getName()).getAsJsonObject();

        if (valueNode.has("color")) {
            this.setValue(new Color(valueNode.get("color").getAsInt(), true));
        }

        if (valueNode.has("rainbowMode")) {
            this.rainbowMode = valueNode.get("rainbowMode").getAsBoolean();
        }

        if (valueNode.has("rainbowSaturation")) {
            this.rainbowSaturation = valueNode.get("rainbowSaturation").getAsFloat();
        }

        if (valueNode.has("rainbowBrightness")) {
            this.rainbowBrightness = valueNode.get("rainbowBrightness").getAsFloat();
        }

        if (valueNode.has("rainbowSpeed")) {
            this.rainbowSpeed = valueNode.get("rainbowSpeed").getAsInt();
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonObject valueNode = new JsonObject();
        valueNode.addProperty("color", this.getValue().getRGB());
        valueNode.addProperty("rainbowMode", this.rainbowMode);
        valueNode.addProperty("rainbowSaturation", this.rainbowSaturation);
        valueNode.addProperty("rainbowBrightness", this.rainbowBrightness);
        valueNode.addProperty("rainbowSpeed", this.rainbowSpeed);
        mainNode.add(getName(), valueNode);
    }

    public Color getValue(int rainbowOffset) {
        if (this.rainbowMode) {
            double rainbowState = Math.ceil((System.currentTimeMillis() * rainbowSpeed + rainbowOffset) / 20.0);
            rainbowState %= 360.0;

            this.setValue(Color.getHSBColor((float) (rainbowState / 360.0F), rainbowSaturation, rainbowBrightness));
        }

        return super.getValue();
    }

    @Override
    public Color getValue() {
        return this.getValue(0);
    }

    @Override
    public void render() {
        final float[] rgba = ColorUtils.rgba(this.getValue().getRGB());

        if (ImGui.checkbox("Rainbow##" + this.getName() + this.getParent().getName(), new ImBoolean(this.rainbowMode))) {
            this.rainbowMode = !this.rainbowMode;
        }

        ImGui.sameLine();

        if (ImGui.colorEdit4("##" + this.getName() + this.getParent().getName(), rgba,
                ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs)) {
            this.setValue(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
        }

        if (this.rainbowMode) {
            final ImInt nextSpeedValue = new ImInt(rainbowSpeed);
            if (ImGui.sliderScalar("Rainbow Speed##" + this.getName() + this.getParent().getName(), ImGuiDataType.S32, nextSpeedValue, 1, 10)) {
                rainbowSpeed = nextSpeedValue.get();
            }

            final ImFloat nextSaturationValue = new ImFloat(rainbowSaturation);
            if (ImGui.sliderScalar("Rainbow Saturation##" + this.getName() + this.getParent().getName(), ImGuiDataType.Float, nextSaturationValue, 0.1f, 1.f, "%.1f")) {
                rainbowSaturation = nextSaturationValue.get();
            }

            final ImFloat nextBrightnessValue = new ImFloat(rainbowBrightness);
            if (ImGui.sliderScalar("Rainbow Brightness##" + this.getName() + this.getParent().getName(), ImGuiDataType.Float, nextBrightnessValue, 0.1f, 1.f, "%.1f")) {
                rainbowBrightness = nextBrightnessValue.get();
            }
        }
    }

    public boolean isRainbowMode() {
        return rainbowMode;
    }

}
