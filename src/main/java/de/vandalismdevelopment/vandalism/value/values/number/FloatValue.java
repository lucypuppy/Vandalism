package de.vandalismdevelopment.vandalism.value.values.number;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.StepNumberValue;
import imgui.ImGui;
import imgui.type.ImFloat;

public class FloatValue extends StepNumberValue<Float> {

    public FloatValue(final String name, final String description, final IValue parent, final float defaultValue, final float step) {
        super(name, description, parent, "float", defaultValue, step);
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
        valueObject.addProperty("value", this.getValue());
    }

    @Override
    public void render() {
        final ImFloat imFloat = new ImFloat(this.getValue());
        if (ImGui.inputFloat("##" + this.getSaveIdentifier(), imFloat, this.getStep())) {
            this.setValue(imFloat.get());
        }
    }

}
