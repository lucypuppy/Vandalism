package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

public class ColorValue extends Value<float[]> {

    private int rgba = 0;

    public ColorValue(final String name, final String description, final IValue parent, final float... defaultValue) {
        super(name, description, parent, defaultValue);
        if (defaultValue.length > 4) {
            throw new IllegalArgumentException("Color Array can't be bigger than 4 entries!");
        }
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(
                valueObject.get("value_red").getAsFloat(),
                valueObject.get("value_green").getAsFloat(),
                valueObject.get("value_blue").getAsFloat(),
                valueObject.get("value_alpha").getAsFloat()
        );
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value_red", this.getValue()[0]);
        valueObject.addProperty("value_green", this.getValue()[1]);
        valueObject.addProperty("value_blue", this.getValue()[2]);
        valueObject.addProperty("value_alpha", this.getValue()[3]);
    }

    @Override
    public void setValue(final float... value) {
        super.setValue(value);
        if (value.length > 4) {
            throw new IllegalArgumentException("Color Array can't be bigger than 4 entries!");
        }
        this.rgba = ColorUtils.rgbaToValueFloat(this.getValue()[0], this.getValue()[0], this.getValue()[0], this.getValue()[0]);
    }

    public int getRGBA() {
        return this.rgba;
    }

    @Override
    public void render() {
        final float[] colorArray = this.getValue();
        if (ImGui.colorEdit4(this.getName() + "##" + this.getHashIdent(), colorArray)) {
            this.setValue(colorArray);
        }
    }

}
