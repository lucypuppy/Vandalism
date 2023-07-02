package me.nekosarekawaii.foxglove.value.value;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(final String name, final String description, final Module parent, final boolean defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        setValue(valueObject.get("value").getAsBoolean());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
