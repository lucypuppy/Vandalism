package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class DoubleValue extends Value<Double> {

    public DoubleValue(String name, String description, Module parent, double defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("double").getAsDouble());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("double", getValue());
    }

}
