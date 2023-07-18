package me.nekosarekawaii.foxglove.value.values.number;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.type.ImInt;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.StepNumberValue;

public class IntegerValue extends StepNumberValue<Integer> {

    public IntegerValue(final String name, final String description, final IValue parent, final int defaultValue) {
        this(name, description, parent, defaultValue, 1);
    }

    public IntegerValue(final String name, final String description, final IValue parent, final int defaultValue, final int step) {
        super(name, description, parent, defaultValue, step);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsInt());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImInt imInt = new ImInt(getValue());
        if (ImGui.inputInt(this.getName() + "##" + this.getHashIdent(), imInt, this.getStep())) {
            this.setValue(imInt.get());
        }
    }

}
