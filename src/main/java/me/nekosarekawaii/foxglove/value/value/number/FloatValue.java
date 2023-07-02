package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.NumberValue;
import me.nekosarekawaii.foxglove.value.Value;

public class FloatValue extends NumberValue<Float> {

    public FloatValue(final String name, final String description, final Module parent, final float defaultValue, final float step) {
        super(name, description, parent, defaultValue, step);
    }

    public FloatValue(final String name, final String description, final Module parent, final float defaultValue) {
        this(name, description, parent, defaultValue, 1.0f);
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsFloat());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
