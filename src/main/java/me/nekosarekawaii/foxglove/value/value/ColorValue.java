package me.nekosarekawaii.foxglove.value.value;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

import java.awt.*;

public class ColorValue extends Value<Color> {

    public ColorValue(String name, String description, Module parent, Color defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(new Color(valueObject.get("value_red").getAsInt(),
                valueObject.get("value_green").getAsInt(),
                valueObject.get("value_blue").getAsInt(),
                valueObject.get("value_alpha").getAsInt()));
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value_red", getValue().getRed());
        valueObject.addProperty("value_green", getValue().getGreen());
        valueObject.addProperty("value_blue", getValue().getBlue());
        valueObject.addProperty("value_alpha", getValue().getAlpha());
    }

}
