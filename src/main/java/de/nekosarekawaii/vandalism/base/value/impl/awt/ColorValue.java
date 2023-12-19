package de.nekosarekawaii.vandalism.base.value.impl.awt;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.common.model.HSBColor;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorValue extends Value<HSBColor> implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final EnumModeValue<ColorMode> colorMode = new EnumModeValue<>(
            this,
            "Color Mode",
            "The color mode",
            ColorMode.STATIC,
            ColorMode.values()
    );

    private final IntegerValue rainbowSpeed = new IntegerValue(
            this,
            "Rainbow Speed",
            "The speed of the rainbow",
            2,
            1,
            10
    );

    // These things arent the client colors these are just the twoColorFade colors and i need them in this class
    private Color mainColorFade;
    private Color secondaryColorFade;

    public ColorValue(ValueParent parent, String name, String description, HSBColor defaultValue) {
        super(parent, name, description, defaultValue);
        resetColor();
    }

    public ColorValue(ValueParent parent, String name, String description, Color defaultValue) {
        this(parent, name, description, new HSBColor(defaultValue));
    }

    private void resetColor() {
        this.mainColorFade = new Color(74, 0, 224);
        this.secondaryColorFade = new Color(142, 45, 226);
    }

    @Override
    public void load(final JsonObject mainNode) {
        final JsonObject valueNode = mainNode.get(getName()).getAsJsonObject();
        this.setValue(new Color(valueNode.get("color").getAsInt(), true));
        this.mainColorFade = new Color(valueNode.get("mainColorFade").getAsInt(), true);
        this.secondaryColorFade = new Color(valueNode.get("secondaryColorFade").getAsInt(), true);

        for (final Value<?> value : this.getValues()) {
            value.load(valueNode);
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonObject valueNode = new JsonObject();
        valueNode.addProperty("color", super.getValue().getColor().getRGB());
        valueNode.addProperty("mainColorFade", this.mainColorFade.getRGB());
        valueNode.addProperty("secondaryColorFade", this.secondaryColorFade.getRGB());

        for (final Value<?> value : this.getValues()) {
            value.save(valueNode);
        }

        mainNode.add(getName(), valueNode);
    }

    public HSBColor getValue(int offset) {
        if (this.colorMode.getValue() == ColorMode.RAINBOW) {
            double rainbowState = Math.ceil((System.currentTimeMillis() * rainbowSpeed.getValue() + offset) / 20.0);
            rainbowState %= 360.0;

            final HSBColor oldValue = super.getValue();
            oldValue.hue = (float) (rainbowState / 360.0);
            this.setValue(oldValue);
        } else if (this.colorMode.getValue() == ColorMode.TWO_COLOR_FADE) {
            final double fade = Math.sin(System.currentTimeMillis() / 600.0D + offset * 0.002D) * 0.5D + 0.5D;
            this.setValue(ColorUtils.colorInterpolate(mainColorFade, secondaryColorFade, fade));
        }

        return super.getValue(); // Do another super.getValue() because the value might have changed
    }

    @Override
    public HSBColor getValue() {
        return this.getValue(0);
    }

    public Color getColor(int offset) {
        return this.getValue(offset).getColor();
    }

    public Color getColor() {
        return this.getColor(0);
    }

    public void setValue(Color color) {
        setValue(new HSBColor(color));
    }

    @Override
    public void render() {
        colorMode.render();
        ImGui.sameLine();

        final int textInputFlags = ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs;
        if (this.colorMode.getValue() == ColorMode.TWO_COLOR_FADE) {
            final float[] mainFadeRgba = ColorUtils.rgba(this.mainColorFade.getRGB());
            final float[] secondFadeRgba = ColorUtils.rgba(this.secondaryColorFade.getRGB());

            if (ImGui.colorEdit4("##mainFadeRgba" + this.getName() + this.getParent().getName(), mainFadeRgba,
                    textInputFlags)) {
                this.mainColorFade = new Color(mainFadeRgba[0], mainFadeRgba[1], mainFadeRgba[2], mainFadeRgba[3]);
            }

            ImGui.sameLine();

            if (ImGui.colorEdit4("##secondFadeRgba" + this.getName() + this.getParent().getName(), secondFadeRgba,
                    textInputFlags)) {
                this.secondaryColorFade = new Color(secondFadeRgba[0], secondFadeRgba[1],
                        secondFadeRgba[2], secondFadeRgba[3]);
            }
        } else {
            final float[] rgba = ColorUtils.rgba(this.getValue().getColor().getRGB());

            if (ImGui.colorEdit4("##rgba" + this.getName() + this.getParent().getName(), rgba,
                    textInputFlags)) {
                this.setValue(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
            }
        }

        if (this.colorMode.getValue() == ColorMode.RAINBOW) {
            rainbowSpeed.render();
        }
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

        resetColor();
        super.resetValue();
    }

    private enum ColorMode implements IName {

        STATIC, RAINBOW, TWO_COLOR_FADE;

        @Override
        public String getName() {
            return StringUtils.normalizeEnumName(this.name());
        }
    }

}
