package de.vandalismdevelopment.vandalism.value.impl.number;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.StepNumberValue;
import imgui.ImGui;
import imgui.type.ImDouble;

public class DoubleValue extends StepNumberValue<Double> {

    public DoubleValue(final String name, final String description, final IValue parent, final double defaultValue, final double step) {
        super(name, description, parent, "double", defaultValue, step);
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
        final ImDouble imDouble = new ImDouble(this.getValue());
        if (ImGui.inputDouble("##" + this.getSaveIdentifier(), imDouble, this.getStep())) {
            this.setValue(imDouble.get());
        }
    }

}
