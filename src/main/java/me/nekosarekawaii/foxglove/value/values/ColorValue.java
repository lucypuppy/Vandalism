package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

import java.awt.*;

public class ColorValue extends Value<Color> {

    public ColorValue(final String name, final String description, final IValue parent, final Color defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(new Color(valueObject.get("value_red").getAsInt(),
                valueObject.get("value_green").getAsInt(),
                valueObject.get("value_blue").getAsInt(),
                valueObject.get("value_alpha").getAsInt()));
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value_red", getValue().getRed());
        valueObject.addProperty("value_green", getValue().getGreen());
        valueObject.addProperty("value_blue", getValue().getBlue());
        valueObject.addProperty("value_alpha", getValue().getAlpha());
    }

    @Override
    public void render() {
        final float[] colorArray = new float[]{getValue().getRed() / 255f, getValue().getGreen() / 255f, getValue().getBlue() / 255f, getValue().getAlpha() / 255f};
        if (ImGui.colorEdit4(getName(), colorArray)) {
            this.setValue(new Color(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
        }
    }

}
