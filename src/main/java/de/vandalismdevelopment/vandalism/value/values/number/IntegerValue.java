package de.vandalismdevelopment.vandalism.value.values.number;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.StepNumberValue;
import imgui.ImGui;
import imgui.type.ImInt;

public class IntegerValue extends StepNumberValue<Integer> {

    public IntegerValue(final String name, final String description, final IValue parent, final int defaultValue) {
        this(name, description, parent, defaultValue, 1);
    }

    public IntegerValue(final String name, final String description, final IValue parent, final int defaultValue, final int step) {
        super(name, description, parent, "integer", defaultValue, step);
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
        ImGui.text(this.getName());
        if (ImGui.inputInt("##" + this.getSaveIdentifier(), imInt, this.getStep())) {
            this.setValue(imInt.get());
        }
    }

}
