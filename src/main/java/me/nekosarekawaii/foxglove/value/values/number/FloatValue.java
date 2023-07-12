package me.nekosarekawaii.foxglove.value.values.number;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.type.ImFloat;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.StepNumberValue;

public class FloatValue extends StepNumberValue<Float> {

    public FloatValue(final String name, final String description, final IValue parent, final float defaultValue, final float step) {
        super(name, description, parent, defaultValue, step);
    }

    public FloatValue(final String name, final String description, final IValue parent, final float defaultValue) {
        this(name, description, parent, defaultValue, 1.0f);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsFloat());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImFloat imFloat = new ImFloat(getValue());
        if (ImGui.inputFloat(this.getName() + "##" + this.getHashIdent(), imFloat, getStep())) {
            this.setValue(imFloat.get());
        }
    }

}
