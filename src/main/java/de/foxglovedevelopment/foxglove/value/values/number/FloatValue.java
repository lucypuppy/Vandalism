package de.foxglovedevelopment.foxglove.value.values.number;

import com.google.gson.JsonObject;
import de.foxglovedevelopment.foxglove.value.IValue;
import de.foxglovedevelopment.foxglove.value.StepNumberValue;
import imgui.ImGui;
import imgui.type.ImFloat;

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
