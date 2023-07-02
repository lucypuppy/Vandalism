package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.NumberValue;
import me.nekosarekawaii.foxglove.value.Value;

public class DoubleValue extends NumberValue<Double> {

    public DoubleValue(final String name, final String description, final Module parent, final double defaultValue, final double step) {
        super(name, description, parent, defaultValue, step);
    }

    public DoubleValue(final String name, final String description, final Module parent, final double defaultValue) {
        this(name, description, parent, defaultValue, 1.0);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsDouble());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
