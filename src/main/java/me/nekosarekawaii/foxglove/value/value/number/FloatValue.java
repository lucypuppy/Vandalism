package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class FloatValue extends Value<Float> {

    public FloatValue(String name, String description, Module parent, float defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("float").getAsFloat());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("float", getValue());
    }

}
