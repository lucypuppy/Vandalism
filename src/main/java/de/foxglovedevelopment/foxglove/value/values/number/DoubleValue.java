package de.foxglovedevelopment.foxglove.value.values.number;

import com.google.gson.JsonObject;
import de.foxglovedevelopment.foxglove.value.IValue;
import de.foxglovedevelopment.foxglove.value.StepNumberValue;
import imgui.ImGui;
import imgui.type.ImDouble;

public class DoubleValue extends StepNumberValue<Double> {

    public DoubleValue(final String name, final String description, final IValue parent, final double defaultValue, final double step) {
        super(name, description, parent, defaultValue, step);
    }

    public DoubleValue(final String name, final String description, final IValue parent, final double defaultValue) {
        this(name, description, parent, defaultValue, 1.0);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsDouble());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImDouble imDouble = new ImDouble(getValue());
        if (ImGui.inputDouble(this.getName() + "##" + this.getHashIdent(), imDouble, getStep())) {
            this.setValue(imDouble.get());
        }
    }

}
