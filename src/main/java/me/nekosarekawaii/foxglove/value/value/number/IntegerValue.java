package me.nekosarekawaii.foxglove.value.value.number;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class IntegerValue extends Value<Integer> {

    private final int step;

    public IntegerValue(final String name, final String description, final Module parent, final int defaultValue) {
        this(name, description, parent, defaultValue, 1);
    }

    public IntegerValue(final String name, final String description, final Module parent, final int defaultValue, final int step) {
        super(name, description, parent, defaultValue);
        this.step = Math.max(step, 1);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        setValue(valueObject.get("int").getAsInt());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("int", getValue());
    }

    public int getStep() {
        return this.step;
    }

}
