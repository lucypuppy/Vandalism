package me.nekosarekawaii.foxglove.value.values.number;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.type.ImInt;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.NumberValue;

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

    @Override
    public void render() {
        final ImInt imInt = new ImInt(getValue());

        if (ImGui.inputInt(getName(), imInt, getStep())) {
            setValue(imInt.get());
        }
    }

}
