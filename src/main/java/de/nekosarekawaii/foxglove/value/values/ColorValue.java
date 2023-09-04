package de.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import de.nekosarekawaii.foxglove.value.IValue;
import de.nekosarekawaii.foxglove.value.Value;
import imgui.ImGui;

import java.awt.*;

public class ColorValue extends Value<Color> {

    public ColorValue(final String name, final String description, final IValue parent, final Color defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(new Color(valueObject.get("value").getAsInt(), true));
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", this.getValue().getRGB());
    }

    @Override
    public void render() {
        final int rgb = this.getValue().getRGB();
        final float[] colorArray = new float[]{
                ((rgb >> 16) & 0xff) / 255F,
                ((rgb >> 8) & 0xff) / 255F,
                ((rgb) & 0xff) / 255F,
                ((rgb >> 24) & 0xff) / 255F
        };

        if (ImGui.colorEdit4(this.getName() + "##" + this.getHashIdent(), colorArray)) {
            this.setValue(new Color(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
        }
    }
}
