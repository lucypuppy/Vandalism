package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.NumberValue;
import me.nekosarekawaii.foxglove.value.Value;

public class IntegerValue extends NumberValue<Integer> {

    public IntegerValue(final String name, final String description, final Module parent, final int defaultValue) {
        this(name, description, parent, defaultValue, 1);
    }

    public IntegerValue(final String name, final String description, final Module parent, final int defaultValue, final int step) {
        super(name, description, parent, defaultValue, step);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        setValue(valueObject.get("value").getAsInt());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
