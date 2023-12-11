package de.vandalismdevelopment.vandalism.base.value.impl.number;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.template.ValueNumber;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImDouble;

public class DoubleValue extends ValueNumber<Double> {

    public DoubleValue(ValueParent parent, String name, String description, Double minValue, Double defaultValue, Double maxValue) {
        super(parent, name, description, minValue, defaultValue, maxValue);
    }

    @Override
    public void load(final JsonObject valueObject) {
        this.setValue(valueObject.get(getName()).getAsDouble());
    }

    @Override
    public void save(final JsonObject valueObject) {
        valueObject.addProperty(getName(), this.getValue());
    }

    @Override
    public void render() {
        final ImDouble nextValue = new ImDouble(this.getValue());
        if (ImGui.sliderScalar("##" + this.getName(), ImGuiDataType.Double, nextValue, this.getMinValue(), this.getMaxValue(), "%.1f")) {
            this.setValue(nextValue.get());
        }
    }

}
