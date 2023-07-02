package me.nekosarekawaii.foxglove.value.value;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(String name, String description, Module parent, boolean defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsBoolean());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
