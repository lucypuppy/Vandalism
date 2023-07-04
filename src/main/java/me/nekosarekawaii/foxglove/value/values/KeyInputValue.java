package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.type.ImInt;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.NumberValue;

public class KeyInputValue extends NumberValue<Integer> {

    public KeyInputValue(final String name, final String description, final IValue parent, final int defaultValue) {
        super(name, description, parent, defaultValue);
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
        if (ImGui.inputInt(getName(), imInt)) {
            setValue(imInt.get());
        }
    }

}
